package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.util.GenericObject.getDefaultValue;
import static net.amygdalum.testrecorder.util.GenericObject.getNonDefaultValue;
import static net.amygdalum.testrecorder.util.Reflections.accessing;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.util.GenericComparison;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedObject;

public class Construction {

	private SimpleDeserializer deserializer;
	private SerializedObject serialized;
	private String name;
	private Object value;
	private Map<Constructor<?>, List<ConstructorParam>> constructors;
	private List<SetterParam> setters;

	public Construction(String name, SerializedObject value) {
		this.deserializer = new SimpleDeserializer();
		this.name = name;
		this.serialized = value;
		this.value = serialized.accept(deserializer);
		this.constructors = new HashMap<>();
		this.setters = new ArrayList<>();
	}

	public Computation computeBest(TypeManager types, Deserializer<Computation> compiler) throws InstantiationException {
		fillOrigins(types);
		return computeConstructionPlans().stream()
			.filter(plan -> GenericComparison.equals(plan.execute(), value))
			.sorted()
			.findFirst()
			.map(plan -> plan.compute(types, compiler))
			.orElseThrow(() -> new InstantiationException());
	}

	private List<ConstructionPlan> computeConstructionPlans() {
		List<ConstructionPlan> constructionsPlans = new ArrayList<>();
		for (Constructor<?> constructor : constructors.keySet()) {
			constructionsPlans.add(computeConstructionPlan(constructor));
		}
		return constructionsPlans;
	}

	private ConstructionPlan computeConstructionPlan(Constructor<?> constructor) {
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
		return new ConstructionPlan(name, constructorParams, setBySetter);
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
		List<Object> bases = new ArrayList<>();
		bases.add(buildFromStandardConstructor(types));
		bases.addAll(buildFromConstructors(types));
		bases.removeIf(Objects::isNull);
		applySetters(bases);
		removeSelfRecursiveConstructions();
	}

	private Object buildFromStandardConstructor(TypeManager types) {
		try {
			Constructor<?> constructor = baseType(serialized.getType()).getDeclaredConstructor();
			if (types.isHidden(constructor)) {
				return null;
			}
			constructor.setAccessible(true);
			constructors.put(constructor, new ArrayList<>());
			return constructor.newInstance();
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}

	private List<Object> buildFromConstructors(TypeManager types) {
		List<Object> objects = new ArrayList<>();

		for (SerializedField field : serialized.getFields()) {
			String fieldName = field.getName();
			Object fieldValue = field.getValue().accept(deserializer);

			for (Constructor<?> constructor : baseType(serialized.getType()).getConstructors()) {
				if (types.isHidden(constructor)) {
					continue;
				}
				
				List<ConstructorParam> params = constructors.computeIfAbsent(constructor, key -> new ArrayList<>());
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				for (int i = 0; i < parameterTypes.length; i++) {
					Class<?> parameterType = parameterTypes[i];
					if (matches(parameterType, fieldValue)) {
						Object uniqueFieldValue = isDefault(parameterType, fieldValue) ? getNonDefaultValue(parameterType) : fieldValue;
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
				Object[] arguments = createArguments(params, parameterTypes);
				try {
					Object result = constructor.newInstance(arguments);
					objects.add(result);
				} catch (ReflectiveOperationException e) {
					continue;
				}
			}
		}
		return objects;
	}

	private void applySetters(List<Object> bases) {
		for (SerializedField field : serialized.getFields()) {
			String fieldName = field.getName();
			Object fieldValue = field.getValue().accept(deserializer);

			nextmethod: for (Method method : baseType(serialized.getType()).getMethods()) {
				if (method.getName().startsWith("set")) {
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length == 1 && matches(parameterTypes[0], fieldValue)) {
						for (Object base : bases) {
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
						setters.add(new SetterParam(method, field, fieldValue));
					}
				}
			}
		}
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
			|| (type.isPrimitive() && value != null && getDefaultValue(type).getClass() == value.getClass());
	}

	private boolean isSet(Object base, String fieldName, Object expectedValue) throws IllegalAccessException {
		Class<?> clazz = base.getClass();
		while (clazz != Object.class) {
			try {
				Field f = clazz.getDeclaredField(fieldName);
				return accessing(f).call(() -> {
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
				clazz = clazz.getSuperclass();
			}
		}
		return false;
	}

	private Object[] createArguments(Object fieldValue, Class<?>[] parameterTypes, int index) {
		Object[] arguments = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			if (i == index) {
				arguments[i] = fieldValue;
			} else {
				arguments[i] = getDefaultValue(parameterTypes[i]);
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
				.orElseGet(() -> getDefaultValue(parameterTypes[paramNumber]));
		}
		return arguments;
	}

}
