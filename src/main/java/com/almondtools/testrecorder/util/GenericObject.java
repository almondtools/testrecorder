package com.almondtools.testrecorder.util;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.almondtools.testrecorder.Wrapped;

public abstract class GenericObject {

	public <T> T as(Class<T> clazz) {
		return as(newInstance(clazz));
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> clazz) {
		Exception exception = null;
		for (Constructor<T> constructor : (Constructor<T>[]) clazz.getDeclaredConstructors()) {
			boolean access = constructor.isAccessible();
			if (!access) {
				constructor.setAccessible(true);
			}
			try {
				T instance = constructor.newInstance(createParams(constructor.getParameterTypes()));
				return instance;
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				exception = e;
			} finally {
				if (!access) {
					constructor.setAccessible(false);
				}
			}
		}
		throw new GenericObjectException(exception);
	}

	public static Object[] createParams(Class<?>[] classes) {
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
		}
		try {
			return new GenericObject() {
			}.as(clazz);
		} catch (GenericObjectException e) {
			return null;
		}

	}

	public <T> T as(Supplier<T> constructor) {
		return as(constructor.get());
	}

	public Wrapped as(Wrapped wrapped) {
		for (Field field : getGenericFields()) {
			try {
				wrapped.setField(field.getName(), field.get(this));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new GenericObjectException(e);
			}
		}
		return wrapped;
	}

	public <T> T as(T o) {
		for (Field field : getGenericFields()) {
			try {
				setField(o, field.getName(), field.get(this));
			} catch (IllegalArgumentException | IllegalAccessException e) {
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
		boolean access = to.isAccessible();
		if (!access) {
			to.setAccessible(true);
		}
		try {
			to.set(o, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new GenericObjectException(e);
		} finally {
			if (!access) {
				to.setAccessible(false);
			}
		}
	}

	public static Field findField(String name, Class<?> clazz) {
		Class<?> current = clazz;
		while (current != Object.class) {
			try {
				return current.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				current = current.getSuperclass();
			}
		}
		throw new GenericObjectException(new NoSuchFieldException(name));
	}

	public List<Field> getGenericFields() {
		Field[] declaredFields = getClass().getDeclaredFields();
		return Stream.of(declaredFields)
			.filter(field -> isSerializable(field))
			.map(field -> {
				field.setAccessible(true);
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
