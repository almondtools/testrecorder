package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.Reflections.accessing;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
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
		return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return null;
			}
		});
	}
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz) {
		Throwable exception = null;
		for (Constructor<T> constructor : (Constructor<T>[]) clazz.getDeclaredConstructors()) {
			try {
				return accessing(constructor).call(() -> {
					Exception innerexception = null;
					for (Supplier<Object[]> params : bestParams(constructor.getParameterTypes())) {
						try {
							return constructor.newInstance(params.get());
						} catch (ReflectiveOperationException | RuntimeException e) {
							innerexception = e;
						}
					}
					throw new RuntimeException(innerexception);
				});
			} catch (ReflectiveOperationException e) {
				exception = e;
			} catch (RuntimeException e) {
				Throwable cause = e.getCause();
				if (cause != null && cause != e) {
					exception = cause;
				} else {
					exception = e;
				}
			}
		}
		if (exception instanceof GenericObjectException) {
			throw new GenericObjectException(exception.getCause());
		} else {
			throw new GenericObjectException(exception);
		}
	}

	private static Iterable<Supplier<Object[]>> bestParams(Class<?>[] parameterTypes) {
		if (parameterTypes.length == 0) {
			return asList(() -> new Object[0]);
		} else {
			return asList(
				() -> createDefaultParams(parameterTypes),
				() -> createNonNullParams(parameterTypes),
				() -> createNonDefaultParams(parameterTypes));
		}
	}

	public static Object[] createDefaultParams(Class<?>[] classes) {
		Object[] params = new Object[classes.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = getDefaultValue(classes[i]);
		}
		return params;
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

	public static Object[] createNonNullParams(Class<?>[] classes) {
		Object[] params = new Object[classes.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = getNonNullValue(classes[i]);
		}
		return params;
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
		} else if (clazz.isArray()) {
			return Array.newInstance(clazz.getComponentType(), 0);
		} else if (clazz.isInterface()) {
			return newProxy(clazz);
		} else {
			return newInstance(clazz);
		}
	}

	public static Object[] createNonDefaultParams(Class<?>[] classes) {
		Object[] params = new Object[classes.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = getNonDefaultValue(classes[i]);
		}
		return params;
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
		Field field = Types.getDeclaredField(clazz, name);
		if (field != null) {
			return field;
		} else {
			throw new GenericObjectException(new NoSuchFieldException(name));
		}
	}

	public List<Field> getGenericFields() {
		Field[] declaredFields = getClass().getDeclaredFields();
		return Stream.of(declaredFields)
			.filter(field -> isSerializable(field))
			.map(field -> {
				return field;
			})
			.collect(toList());
	}

	private boolean isSerializable(Field field) {
		return !field.isSynthetic()
			&& field.getName().indexOf('$') < 0
			&& ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
			&& ((field.getModifiers() & Modifier.FINAL) != Modifier.FINAL);
	}

}
