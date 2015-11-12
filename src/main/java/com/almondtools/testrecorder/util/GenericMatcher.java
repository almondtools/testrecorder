package com.almondtools.testrecorder.util;

import static com.almondtools.testrecorder.util.GenericComparison.getValue;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Queue;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.TypeSafeMatcher;

public class GenericMatcher extends GenericObject {

	public <T> Matcher<T> matching(Class<T> clazz) {
		return new InternalsMatcher<T>();
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

	private class InternalsMatcher<T> extends TypeSafeMatcher<T> {
		
		@Override
		public void describeTo(Description description) {
			description.appendText("with fields:");
			for (Field field : getGenericFields()) {
				describeField(description, field, GenericMatcher.this);
			}
		}

		@Override
		protected boolean matchesSafely(T item) {
			return GenericMatcher.this.matches(item);
		}

		@Override
		protected void describeMismatchSafely(T item, Description mismatchDescription) {
			mismatchDescription.appendText("with fields:");
			for (Field field : item.getClass().getDeclaredFields()) {
				describeField(mismatchDescription, field, item);
			}
		}

		private void describeField(Description description, Field field, Object object) {
			try {
				description.appendText("\n\t")
					.appendText(field.getType().getSimpleName()).appendText(" ")
					.appendText(field.getName()).appendText(":");
				Object value = getValue(field, object);
				if (value instanceof SelfDescribing) {
					description.appendDescriptionOf((SelfDescribing) value);
				} else {
					description.appendValue(value);
				}
			} catch (ReflectiveOperationException e) {
				description.appendText("\n\t")
					.appendValue(field.getType()).appendText(" ")
					.appendValue(field.getName()).appendText(":<description failed>");
			}
		}

	}

}
