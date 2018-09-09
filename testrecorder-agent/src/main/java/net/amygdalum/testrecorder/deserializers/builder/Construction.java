package net.amygdalum.testrecorder.deserializers.builder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.runtime.DefaultComparisonStrategy.all;
import static net.amygdalum.testrecorder.runtime.SelectedFieldsComparisonStrategy.comparingFields;
import static net.amygdalum.testrecorder.util.Reflections.accessing;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.resolve;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import net.amygdalum.testrecorder.deserializers.Deserializer;
import net.amygdalum.testrecorder.deserializers.SimpleDeserializer;
import net.amygdalum.testrecorder.hints.Setter;
import net.amygdalum.testrecorder.runtime.DefaultValue;
import net.amygdalum.testrecorder.runtime.GenericComparison;
import net.amygdalum.testrecorder.runtime.NonDefaultValue;
import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.LocalVariable;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.TypeManager;
import net.amygdalum.testrecorder.util.Types;
import net.amygdalum.testrecorder.values.SerializedObject;

public class Construction {

	private SimpleDeserializer deserializer;
	private SerializedObject serialized;
	private LocalVariable var;
	private Object value;
	private Map<Constructor<?>, List<ConstructorParam>> constructors;
	private List<SetterParam> setters;

	public Construction(DeserializerContext context, LocalVariable var, SerializedObject value) {
		this.deserializer = new SimpleDeserializer(context);
		this.var = var;
		this.serialized = value;
		this.value = serialized.accept(deserializer);
		this.constructors = new HashMap<>();
		this.setters = new ArrayList<>();
	}

	public Computation computeBest(TypeManager types, Deserializer generator) throws InstantiationException {
		if (types.isHidden(serialized.getType())) {
			throw new InstantiationException();
		}
		fillOrigins(types);

		List<String> fields = getFields();

		return computeConstructionPlans().stream()
			.map(plan -> plan.disambiguate(constructors.keySet()))
			.filter(plan -> GenericComparison.equals("", plan.execute(), value, comparingFields(fields).andThen(all())))
			.sorted()
			.findFirst()
			.map(plan -> plan.compute(types, generator))
			.orElseThrow(() -> new InstantiationException());
	}

	private List<String> getFields() {
		return serialized.getFields().stream()
			.map(field -> field.getName())
			.collect(toList());
	}

	private List<ConstructionPlan> computeConstructionPlans() {
		List<ConstructionPlan> constructionsPlans = new ArrayList<>();
		for (Constructor<?> constructor : constructors.keySet()) {
			computeConstructionPlan(constructor).ifPresent(constructionsPlans::add);
		}
		return constructionsPlans;
	}

	private Optional<ConstructionPlan> computeConstructionPlan(Constructor<?> constructor) {
		Set<SerializedField> todo = new HashSet<>(serialized.getFields());
		ConstructorParams constructorParams = computeConstructorParams(constructor, todo);
		List<SetterParam> setBySetter = new ArrayList<>();
		for (SetterParam param : setters) {
			SerializedField field = param.getField();
			if (todo.contains(field)) {
				todo.remove(field);
				setBySetter.add(param);
			}
		}
		todo.removeIf(this::isImmutable);
		if (todo.isEmpty()) {
			return Optional.of(new ConstructionPlan(var, constructorParams, setBySetter));
		} else {
			return Optional.empty();
		}
	}

	private boolean isImmutable(SerializedField field) {
		SerializedValue value = field.getValue();
		if (value instanceof SerializedImmutableType) {
			return true;
		} else if (value instanceof SerializedReferenceType) {
			DeserializerContext context = deserializer.getContext();
			if (context.refCount(value) > 1) {
				return false;
			}
			return context.closureOf(value).stream()
				.filter(val -> val instanceof SerializedReferenceType && !(val instanceof SerializedImmutableType))
				.allMatch(val -> context.refCount(val) <= 1);
		} else {
			return true;
		}
	}

	public ConstructorParams computeConstructorParams(Constructor<?> constructor, Set<SerializedField> todo) {
		List<ConstructorParam> setByConstructor = constructors.get(constructor);
		for (ConstructorParam param : setByConstructor) {
			todo.remove(param.getField());
		}
		return constructorOf(constructor, setByConstructor);
	}

	private ConstructorParams constructorOf(Constructor<?> constructor, List<ConstructorParam> params) {
		ConstructorParams constructorParams = new ConstructorParams(constructor);
		for (ConstructorParam param : params) {
			constructorParams.add(param);
		}
		return constructorParams;
	}

	private void fillOrigins(TypeManager types) {
		addStandardConstructor(types);
		addSuitableConstructors(types);
		applySetters(types);
		removeSelfRecursiveConstructions();
	}

	private void addStandardConstructor(TypeManager types) {
		try {
			Constructor<?> constructor = Types.getDeclaredConstructor(baseType(serialized.getType()));
			if (types.isHidden(constructor)) {
				return;
			}
			constructor.setAccessible(true);
			constructors.put(constructor, new ArrayList<>());
		} catch (ReflectiveOperationException e) {
			return;
		}
	}

	private void addSuitableConstructors(TypeManager types) {
		for (SerializedField field : serialized.getFields()) {
			String fieldName = field.getName();
			Object fieldValue = field.getValue().accept(deserializer);

			for (Constructor<?> constructor : getParameterConstructors(types, serialized.getType())) {

				List<ConstructorParam> params = constructors.computeIfAbsent(constructor, key -> new ArrayList<>());
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				for (int i = 0; i < parameterTypes.length; i++) {
					Class<?> parameterType = parameterTypes[i];
					if (matches(parameterType, fieldValue)) {
						Object uniqueFieldValue = isDefault(parameterType, fieldValue)
							? NonDefaultValue.of(parameterType)
							: fieldValue;
						Object[] arguments = createArguments(uniqueFieldValue, parameterTypes, i);
						try {
							Object result = constructor.newInstance(arguments);
							if (isSet(result, fieldName, uniqueFieldValue)) {
								params.add(new ConstructorParam(constructor, i, field, fieldValue));
							}
						} catch (ReflectiveOperationException e) {
							continue;
						}
					}
				}
			}
		}
	}

	private List<Constructor<?>> getParameterConstructors(TypeManager types, Type type) {
		return Arrays.stream(baseType(type).getConstructors())
			.filter(constructor -> !types.isHidden(constructor))
			.collect(toList());
	}

	private List<Object> createBases() {
		return constructors.entrySet().stream()
			.map(entry -> createBase(entry.getKey(), entry.getValue()))
			.filter(Objects::nonNull)
			.collect(toList());
	}

	private Object createBase(Constructor<?> constructor, List<ConstructorParam> params) {
		Object[] arguments = createArguments(params, constructor.getParameterTypes());
		try {
			return constructor.newInstance(arguments);
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}

	private void applySetters(TypeManager types) {
		for (SerializedField field : serialized.getFields()) {
			String fieldName = field.getName();
			Object fieldValue = field.getValue().accept(deserializer);

			nextmethod: for (Method method : getSetterMethods(types, serialized.getType(), fieldValue)) {
				for (Object base : createBases()) {
					try {
						if (isSet(base, fieldName, fieldValue)) {
							//default value is already matching, no need to apply a setter
							continue nextmethod;
						}
						method.invoke(base, fieldValue);
						if (!isSet(base, fieldName, fieldValue)) {
							//did not set the correct value
							continue nextmethod;
						}
					} catch (ReflectiveOperationException e) {
						//unexpected exception on setting value
						continue nextmethod;
					}
				}
				Type type = resolve(method.getGenericParameterTypes()[0], baseType(serialized.getType()));
				setters.add(new SetterParam(method, type, field, fieldValue));
			}
		}
	}

	private List<Method> getSetterMethods(TypeManager types, Type type, Object value) {
		return Arrays.stream(baseType(type).getMethods())
			.filter(method -> !types.isHidden(method))
			.filter(method -> qualifiesAsSetter(method, value))
			.collect(toList());
	}

	private boolean qualifiesAsSetter(Method method, Object value) {
		if (!method.getName().startsWith("set") && !method.isAnnotationPresent(Setter.class)) {
			return false;
		}
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length != 1) {
			return false;
		}
		if (!matches(parameterTypes[0], value)) {
			return false;
		}
		return true;
	}

	private void removeSelfRecursiveConstructions() {
		constructors.values().removeIf(params -> {
			return params.stream()
				.map(param -> param.getField().getValue())
				.anyMatch(value -> closure(value).contains(serialized));
		});
	}

	private Set<SerializedValue> closure(SerializedValue root) {
		Set<SerializedValue> closure = new HashSet<>();

		Queue<SerializedValue> todo = new LinkedList<>();
		todo.add(root);
		while (!todo.isEmpty()) {
			SerializedValue current = todo.poll();
			if (closure.contains(current)) {
				continue;
			} else {
				closure.add(current);
				todo.addAll(current.referencedValues());
			}
		}

		return closure;
	}

	private boolean matches(Class<?> type, Object value) {
		return isDefault(type, value)
			|| type.isInstance(value);
	}

	private boolean isDefault(Class<?> type, Object value) {
		return (!type.isPrimitive() && value == null)
			|| (type.isPrimitive() && value != null && DefaultValue.of(type).getClass() == value.getClass());
	}

	private boolean isSet(Object base, String fieldName, Object expectedValue) throws IllegalAccessException {
		try {
			Field field = Types.getDeclaredField(base.getClass(), fieldName);
			return accessing(field).call(f -> {
				Object foundValue = f.get(base);
				if (foundValue == expectedValue) {
					return true;
				} else if (foundValue == null || expectedValue == null) {
					return false;
				} else {
					return foundValue.equals(expectedValue);
				}
			});

		} catch (ReflectiveOperationException e) {
			return false;
		}
	}

	private Object[] createArguments(Object fieldValue, Class<?>[] parameterTypes, int index) {
		Object[] arguments = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			if (i == index) {
				arguments[i] = fieldValue;
			} else {
				arguments[i] = DefaultValue.of(parameterTypes[i]);
			}
		}
		return arguments;
	}

	private Object[] createArguments(List<ConstructorParam> params, Class<?>[] parameterTypes) {
		Object[] arguments = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			final int paramNumber = i;
			arguments[i] = params.stream()
				.filter(param -> param.getParamNumber() == paramNumber)
				.map(param -> param.getValue())
				.filter(Objects::nonNull)
				.findFirst()
				.orElseGet(() -> DefaultValue.of(parameterTypes[paramNumber]));
		}
		return arguments;
	}

}
