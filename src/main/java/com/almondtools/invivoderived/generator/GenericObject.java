package com.almondtools.invivoderived.generator;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.TypeSafeMatcher;

public abstract class GenericObject {

	public <T> T as(Class<T> clazz) {
		return as(newInstance(clazz));
	}

	@SuppressWarnings("unchecked")
	private <T> T newInstance(Class<T> clazz) {
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

	private Object[] createParams(Class<?>[] classes) {
		Object[] params = new Object[classes.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = getDefaultValue(classes[i]);
		}
		return params;
	}

	private Object getDefaultValue(Class<?> clazz) {
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
		} else{
			return null;
		}
	}

	public <T> T as(Supplier<T> constructor) {
		return as(constructor.get());
	}

	public <T> T as(T o) {
		for (Field field : getGenericFields()) {
			Field to = findField(field.getName(), o.getClass());
			boolean access = to.isAccessible();
			if (!access) {
				to.setAccessible(true);
			}
			try {
				to.set(o, field.get(this));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new GenericObjectException(e);
			} finally {
				if (!access) {
					to.setAccessible(false);
				}
			}
		}
		return o;
	}

	private Field findField(String name, Class<?> clazz) {
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


	private List<Field> getGenericFields() {
		Field[] declaredFields = getClass().getDeclaredFields();
		return Stream.of(declaredFields)
			.filter(field -> isSerializable(field))
			.map(field -> {field.setAccessible(true); return field;})
			.collect(toList());
	}
	
	private boolean isSerializable(Field field) {
		return !field.isSynthetic()
			&& field.getName().indexOf('$') < 0
			&& ((field.getModifiers() & Modifier.STATIC) != Modifier.STATIC)
			&& ((field.getModifiers() & Modifier.FINAL) != Modifier.FINAL);
	}

	public boolean matches(Object o) {
		for (Field field : getGenericFields()) {
			Field to = findField(field.getName(), o.getClass());
			boolean access = to.isAccessible();
			if (!access) {
				to.setAccessible(true);
			}
			try {
				Object fieldValue = field.get(this);
				Object toValue = to.get(o);
				if (fieldValue == null && toValue == null) {
					continue;
				} else if (fieldValue instanceof Matcher<?>) {
					if (!((Matcher<?>) fieldValue).matches(toValue)) {
						return false;
					} else {
						continue;
					}
				} else if (fieldValue == null) {
					return false;
				} else if (toValue == null) {
					return false;
				} else if (!fieldValue.equals(toValue)) {
					return false;
				} else {
					continue;
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new GenericObjectException(e);
			} finally {
				if (!access) {
					to.setAccessible(false);
				}
			}
		}
		return true;
	}

	public <T> Matcher<T> matcher(Class<T> clazz) {
		GenericObject self = GenericObject.this;
		return new TypeSafeMatcher<T>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("with fields:");
				for (Field field : getGenericFields()) {
					try {
						description.appendText("\n\t")
							.appendText(field.getType().getSimpleName()).appendText(" ")
							.appendText(field.getName()).appendText(":");
						Object value = field.get(self);
						if (value instanceof SelfDescribing) {
							description.appendDescriptionOf((SelfDescribing) value);
						} else {
							description.appendValue(value);
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						description.appendText("\n\t")
							.appendValue(field.getType()).appendText(" ")
							.appendValue(field.getName()).appendText(":<description failed>");
					}
				}
			}

			@Override
			protected boolean matchesSafely(T item) {
				return self.matches(item);
			}

			@Override
			protected void describeMismatchSafely(T item, Description mismatchDescription) {
				mismatchDescription.appendText("with fields:");
				for (Field field : item.getClass().getDeclaredFields()) {
					try {
						mismatchDescription.appendText("\n")
							.appendText(field.getType().getSimpleName()).appendText(" ")
							.appendText(field.getName()).appendText(":")
							.appendValue(getValue(field, item));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						mismatchDescription.appendText("\n")
							.appendValue(field.getType()).appendText(" ")
							.appendValue(field.getName()).appendText(":<description failed>");
					}
				}
			}

			private Object getValue(Field field, Object item) throws IllegalAccessException {
				boolean access = field.isAccessible();
				if (!access) {
					field.setAccessible(true);
				}
				try {
					return field.get(item);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new GenericObjectException(e);
				} finally {
					if (!access) {
						field.setAccessible(false);
					}
				}
			}

		};
	}

}
