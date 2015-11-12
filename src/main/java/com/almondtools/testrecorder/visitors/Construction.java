package com.almondtools.testrecorder.visitors;

import static com.almondtools.testrecorder.GenericObject.getDefaultValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.almondtools.testrecorder.GenericComparison;
import com.almondtools.testrecorder.SerializedValueVisitor;
import com.almondtools.testrecorder.values.SerializedField;
import com.almondtools.testrecorder.values.SerializedObject;

public class Construction {

	private Deserializer deserializer;
	private LocalVariableNameGenerator locals;
	private SerializedObject serialized;
	private Object value;
	private Map<Constructor<?>, List<ConstructorParam>> constructors;
	private List<SetterParam> setters;

	public Construction(LocalVariableNameGenerator locals, SerializedObject value) {
		this.deserializer = new Deserializer();
		this.locals = locals;
		this.serialized = value;
		this.value = serialized.accept(deserializer);
		this.constructors = new HashMap<>();
		this.setters = new ArrayList<>();
	}

	public Computation computeBest(SerializedValueVisitor<Computation> compiler) throws InstantiationException {
		fillOrigins();
		return computeConstructionPlans().stream()
			.filter(plan -> GenericComparison.equals(plan.execute(), value))
			.sorted()
			.findFirst()
			.map(plan -> plan.compute(compiler))
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
		List<ConstructorParam> setByConstructor = constructors.get(constructor);
		for (ConstructorParam param : setByConstructor) {
			todo.remove(param.getField());
		}
		List<SetterParam> setBySetter = new ArrayList<>();
		for (SetterParam param : setters) {
			SerializedField field = param.getField();
			if (todo.contains(field)) {
				todo.remove(field);
				setBySetter.add(param);
			}
		}
		ConstructorParams constructorOf = constructorOf(constructor, setByConstructor);
		return new ConstructionPlan(locals, constructorOf, setBySetter);
	}

	private ConstructorParams constructorOf(Constructor<?> constructor, List<ConstructorParam> params) {
		ConstructorParams constructorParams = new ConstructorParams(constructor);
		for (ConstructorParam param : params) {
			constructorParams.add(param);
		}
		return constructorParams;
	}

	private void fillOrigins() {
		List<Object> bases = new ArrayList<>();
		bases.add(buildFromStandardConstructor());
		bases.addAll(buildFromConstructors());
		bases.removeIf(Objects::isNull);
		applySetters(bases);
	}

	private Object buildFromStandardConstructor() {
		try {
			Constructor<?> constructor = serialized.getObjectType().getConstructor();
			constructors.put(constructor, new ArrayList<>());
			return constructor.newInstance();
		} catch (ReflectiveOperationException e) {
			return null;
		}
	}

	private List<Object> buildFromConstructors() {
		List<Object> objects = new ArrayList<>();
		for (SerializedField field : serialized.getFields()) {
			String fieldName = field.getName();
			Object fieldValue = field.getValue().accept(deserializer);

			for (Constructor<?> constructor : serialized.getObjectType().getConstructors()) {
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				for (int i = 0; i < parameterTypes.length; i++) {
					Class<?> parameterType = parameterTypes[i];
					if (matches(parameterType, fieldValue)) {
						Object[] arguments = createArguments(fieldValue, parameterTypes, i);
						try {
							Object result = constructor.newInstance(arguments);
							objects.add(result);
							if (isSet(result, fieldName, fieldValue)) {
								List<ConstructorParam> params = constructors.computeIfAbsent(constructor, key -> new ArrayList<>());
								params.add(new ConstructorParam(constructor, i, field, fieldValue));
							}
						} catch (ReflectiveOperationException e) {
							continue;
						}
					}
				}
			}
		}
		return objects;
	}

	private void applySetters(List<Object> bases) {
		for (SerializedField field : serialized.getFields()) {
			String fieldName = field.getName();
			Object fieldValue = field.getValue().accept(deserializer);

			nextmethod: for (Method method : serialized.getObjectType().getMethods()) {
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

	private boolean matches(Class<?> type, Object value) {
		return (!type.isPrimitive() && value == null)
			|| (type.isPrimitive() && value != null && getDefaultValue(type).getClass() == value.getClass())
			|| type.isInstance(value);
	}

	private boolean isSet(Object base, String fieldName, Object expectedValue) throws IllegalAccessException {
		Class<?> clazz = base.getClass();
		while (clazz != Object.class) {
			try {
				Field f = clazz.getDeclaredField(fieldName);
				boolean accessible = f.isAccessible();
				try {
					if (!accessible) {
						f.setAccessible(true);
					}
					Object foundValue = f.get(base);
					if (foundValue == expectedValue) {
						return true;
					} else if (foundValue == null || expectedValue == null) {
						return false;
					} else {
						return foundValue.equals(expectedValue);
					}
				} finally {
					if (!accessible) {
						f.setAccessible(true);
					}
				}
			} catch (NoSuchFieldException e) {
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

}
