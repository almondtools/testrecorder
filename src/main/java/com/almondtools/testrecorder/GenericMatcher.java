package com.almondtools.testrecorder;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Queue;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.TypeSafeMatcher;

public class GenericMatcher extends GenericObject {

	public <T> Matcher<T> matcher(Class<T> clazz) {
		GenericMatcher self = GenericMatcher.this;
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

	public boolean matches(Object o) {
		Queue<GenericComparison> remainder = new LinkedList<>(); 
		for (Field field : getGenericFields()) {
			Field to = findField(field.getName(), o.getClass());
			if (!GenericComparison.equals(this, field, o, to, remainder)) {
				return false;
			}
		}
		while (!remainder.isEmpty()) {
			GenericComparison current = remainder.remove();
			if (current.getLeft() instanceof Matcher<?>) {
				Matcher<?> matcher = (Matcher<?>) current.getLeft();
				Object value = current.getRight();
				if (!matcher.matches(value)) {
					return false;
				}
			} else if (!current.eval(remainder)) {
				return false;
			}
		}
		return true;
	}

}
