package com.almondtools.iit.generator;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.TypeSafeMatcher;


public abstract class GenericObject {

	public <T> T as(Class<T> clazz) {
		try {
			return as(clazz.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new GenericObjectException(e);
		}
	}

	public <T> T as(Supplier<T> constructor) {
		return as(constructor.get());
	}

	public <T> T as(T o) {
		for (Field field : getClass().getFields()) {
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

	public boolean matches(Object o) {
		for (Field field : getClass().getFields()) {
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
				for (Field field : self.getClass().getFields()) {
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
				for (Field field : item.getClass().getFields()) {
					try {
						mismatchDescription.appendText("\n")
							.appendText(field.getType().getSimpleName()).appendText(" ")
							.appendText(field.getName()).appendText(":")
							.appendValue(field.get(item));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						mismatchDescription.appendText("\n")
							.appendValue(field.getType()).appendText(" ")
							.appendValue(field.getName()).appendText(":<description failed>");
					}
				}
			}

		};
	}

}
