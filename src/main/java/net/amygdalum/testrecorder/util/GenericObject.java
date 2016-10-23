package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.Params.NONE;
import static net.amygdalum.testrecorder.util.Reflections.accessing;
import static net.amygdalum.testrecorder.util.Types.getDeclaredField;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.Wrapped;

public abstract class GenericObject {

	public static <T> T forward(Class<T> clazz) {
		return newInstance(clazz);
	}

	public static void define(Object o, GenericObject genericObject) {
		for (Field field : genericObject.getGenericFields()) {
			try {
				accessing(field).exec(() -> setField(o, field.getName(), field.get(genericObject)));
			} catch (ReflectiveOperationException e) {
				throw new GenericObjectException(e);
			}
		}
	}

	public <T> T as(Class<T> clazz) {
		return as(newInstance(clazz));
	}

	@SuppressWarnings("unchecked")
	public static <T> T newProxy(Class<T> clazz) {
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] { clazz }, new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return null;
			}
		});
	}

	@SuppressWarnings({ "unchecked", "restriction" })
	public static <T> T newInstance(Class<T> clazz) {
		List<String> tries = new ArrayList<>();
		for (Constructor<T> constructor : (Constructor<T>[]) clazz.getDeclaredConstructors()) {
			try {
				return accessing(constructor).call(() -> {
					List<String> innertries = new ArrayList<>();
					for (Params params : bestParams(constructor.getParameterTypes())) {
						try {
							return constructor.newInstance(params.values());
						} catch (ReflectiveOperationException | RuntimeException e) {
							innertries.add(params.getDescription());
						}
					}
					throw new FailedInstantiationException(clazz, innertries);
				});
			} catch (ReflectiveOperationException e) {
				throw new GenericObjectException(e);
			} catch (FailedInstantiationException e) {
				tries.addAll(e.getTries());
			}
		}
		try {
			sun.reflect.ReflectionFactory rf = sun.reflect.ReflectionFactory.getReflectionFactory();
			Constructor<T> serializationConstructor = (Constructor<T>) rf.newConstructorForSerialization(clazz, Object.class.getDeclaredConstructor());
			return clazz.cast(serializationConstructor.newInstance());
		} catch (ReflectiveOperationException | Error e) {
			throw new FailedInstantiationException(clazz, tries);
		}
	}

	private static Iterable<Params> bestParams(Class<?>[] parameterTypes) {
		if (parameterTypes.length == 0) {
			return asList(NONE);
		} else {
			return asList(new DefaultParams(parameterTypes), new NonNullParams(parameterTypes), new NonDefaultParams(parameterTypes));
		}
	}

	public static Object getDefaultValue(Class<?> clazz) {
		if (clazz == boolean.class) {
			return false;
		} else if (clazz == char.class) {
			return (char) 0;
		} else if (clazz == byte.class) {
			return (byte) 0;
		} else if (clazz == short.class) {
			return (short) 0;
		} else if (clazz == int.class) {
			return (int) 0;
		} else if (clazz == float.class) {
			return (float) 0;
		} else if (clazz == long.class) {
			return (long) 0;
		} else if (clazz == double.class) {
			return (double) 0;
		} else {
			return null;
		}
	}

	public static Object getNonNullValue(Class<?> clazz) {
		if (clazz == boolean.class) {
			return false;
		} else if (clazz == char.class) {
			return (char) 0;
		} else if (clazz == byte.class) {
			return (byte) 0;
		} else if (clazz == short.class) {
			return (short) 0;
		} else if (clazz == int.class) {
			return (int) 0;
		} else if (clazz == float.class) {
			return (float) 0;
		} else if (clazz == long.class) {
			return (long) 0;
		} else if (clazz == double.class) {
			return (double) 0;
		} else if (clazz == String.class) {
			return "";
		} else if (clazz.isArray()) {
			return Array.newInstance(clazz.getComponentType(), 0);
		} else if (clazz.isInterface()) {
			return newProxy(clazz);
		} else {
			return newInstance(clazz);
		}
	}

	public static Object getNonDefaultValue(Class<?> clazz) {
		if (clazz == boolean.class) {
			return true;
		} else if (clazz == char.class) {
			return (char) 1;
		} else if (clazz == byte.class) {
			return (byte) 1;
		} else if (clazz == short.class) {
			return (short) 1;
		} else if (clazz == int.class) {
			return (int) 1;
		} else if (clazz == float.class) {
			return (float) 1;
		} else if (clazz == long.class) {
			return (long) 1;
		} else if (clazz == double.class) {
			return (double) 1;
		} else {
			return getNonNullValue(clazz);
		}

	}

	public <T> T as(Supplier<T> constructor) {
		return as(constructor.get());
	}

	public Wrapped as(Wrapped wrapped) {
		for (Field field : getGenericFields()) {
			try {
				accessing(field).exec(() -> wrapped.setField(field.getName(), field.get(this)));
			} catch (ReflectiveOperationException e) {
				throw new GenericObjectException(e);
			}
		}
		return wrapped;
	}

	public <T> T as(T o) {
		for (Field field : getGenericFields()) {
			try {
				accessing(field).exec(() -> setField(o, field.getName(), field.get(this)));
			} catch (ReflectiveOperationException e) {
				throw new GenericObjectException(e);
			}
		}
		return o;
	}

	public static void setField(Object o, String name, Object value) {
		if (value instanceof Wrapped) {
			value = ((Wrapped) value).value();
		}
		Field to = findField(name, o.getClass());
		setField(o, to, value);
	}

	public static void setField(Object o, Field to, Object value) {
		try {
			accessing(to).exec(() -> to.set(o, value));
		} catch (ReflectiveOperationException e) {
			throw new GenericObjectException(e);
		}
	}

	public static void copyArrayValues(Object from, Object to) {
		int fromLength = Array.getLength(from);
		int toLength = Array.getLength(to);
		int minLength = fromLength < toLength ? fromLength : toLength;
		for (int i = 0; i < minLength; i++) {
			Object value = Array.get(from, i);
			Array.set(to, i, value);
		}
	}

	public static void copyField(Field field, Object from, Object to) {
		try {
			accessing(field).exec(() -> {
				Object value = field.get(from);
				field.set(to, value);
			});
		} catch (ReflectiveOperationException e) {
			throw new GenericObjectException(e);
		}
	}

	public static Field findField(String name, Class<?> clazz) {
		try {
			return getDeclaredField(clazz, name);
		} catch (NoSuchFieldException e) {
			throw new GenericObjectException(e);
		}
	}

	public List<Field> getGenericFields() {
		Field[] declaredFields = getClass().getDeclaredFields();
		return Stream.of(declaredFields).filter(field -> isSerializable(field)).map(field -> {
			return field;
		}).collect(toList());
	}

	private boolean isSerializable(Field field) {
		return !field.isSynthetic() && field.getName().indexOf('$') < 0
				&& ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
				&& ((field.getModifiers() & Modifier.FINAL) != Modifier.FINAL);
	}

	private static class DefaultParams extends Params {

		public DefaultParams(Class<?>[] classes) {
			super(classes);
		}

		@Override
		public Object getValue(Class<?> clazz) {
			return getDefaultValue(clazz);
		}

	}

	private static class NonNullParams extends Params {
		public NonNullParams(Class<?>[] classes) {
			super(classes);
		}

		@Override
		public String getDescription(Class<?> clazz) {
			if (clazz.isPrimitive()) {
				return super.getDescription(clazz);
			} else if (clazz.isArray()) {
				return "new " + clazz.getComponentType().getSimpleName() + "[0]";
			} else if (clazz.isInterface()) {
				return "proxy " + clazz.getSimpleName() + "()";
			} else {
				return "new " + clazz.getSimpleName() + "()";
			}
		}

		@Override
		public Object getValue(Class<?> clazz) {
			return getNonNullValue(clazz);
		}

	}

	private static class NonDefaultParams extends Params {

		public NonDefaultParams(Class<?>[] classes) {
			super(classes);
		}

		@Override
		public String getDescription(Class<?> clazz) {
			if (clazz.isPrimitive()) {
				return super.getDescription(clazz);
			} else if (clazz.isArray()) {
				return "new " + clazz.getComponentType().getSimpleName() + "[0]";
			} else if (clazz.isInterface()) {
				return "proxy " + clazz.getSimpleName() + "()";
			} else {
				return "new " + clazz.getSimpleName() + "()";
			}
		}

		@Override
		public Object getValue(Class<?> clazz) {
			return getNonDefaultValue(clazz);
		}

	}

}
