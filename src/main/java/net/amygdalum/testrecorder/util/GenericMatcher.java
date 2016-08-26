package net.amygdalum.testrecorder.util;

import static net.amygdalum.testrecorder.util.GenericComparison.getValue;
import static org.hamcrest.Matchers.instanceOf;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.TypeSafeMatcher;

import net.amygdalum.testrecorder.Wrapped;

public class GenericMatcher extends GenericObject {

	public <T> Matcher<T> matching(Class<T> clazz) {
		return new InternalsMatcher<T>(clazz);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Matcher<Object> matching(Wrapped wrapped) {
		return (Matcher) new InternalsMatcher(wrapped.getWrappedClass());
	}

	public <T, S> Matcher<S> matching(Class<T> clazz, Class<S> to) {
		return new CastingMatcher<>(to, new InternalsMatcher<T>(clazz));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Matcher<Wrapped> matching(Wrapped clazz, Wrapped to) {
		return (Matcher) new CastingMatcher(to.getWrappedClass(), new InternalsMatcher(clazz.getWrappedClass()));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <S> Matcher<S> matching(Wrapped clazz, Class<S> to) {
		return (Matcher) new CastingMatcher(to, new InternalsMatcher(clazz.getWrappedClass()));
	}

	public boolean matches(Object o) {
		WorkSet<GenericComparison> remainder = new WorkSet<>();
		for (Field field : getGenericFields()) {
			Field to = findField(field.getName(), o.getClass());
			if (!GenericComparison.equals(this, field, o, to, remainder)) {
				return false;
			}
		}
		while (remainder.hasMoreElements()) {
			GenericComparison current = remainder.dequeue();
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

	public static <T> Matcher<T> recursive(Class<T> clazz) {
		return instanceOf(clazz);
	}

	public static Matcher<?> recursive(Wrapped wrapped) {
		return instanceOf(wrapped.getWrappedClass());
	}

	private class InternalsMatcher<T> extends TypeSafeMatcher<T> {

		private Class<T> clazz;

		public InternalsMatcher(Class<T> clazz) {
			this.clazz = clazz;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("of class : ").appendValue(clazz.getName()).appendText("\n");
			description.appendText("with fields:");
			for (Field field : getGenericFields()) {
				describeField(description, field, GenericMatcher.this);
			}
		}

		@Override
		protected boolean matchesSafely(T item) {
			return clazz == item.getClass()
				&& GenericMatcher.this.matches(item);
		}

		@Override
		protected void describeMismatchSafely(T item, Description mismatchDescription) {
			if (item == null) {
				mismatchDescription.appendText("is").appendValue(null);
			} else {
				mismatchDescription.appendText("of class : ").appendValue(item.getClass().getName()).appendText("\n");
				mismatchDescription.appendText("with fields:");
				for (Field field : fields(item.getClass())) {
					describeField(mismatchDescription, field, item);
				}
			}
		}

		private List<Field> fields(Class<?> clazz) {
			List<Field> fields = new ArrayList<>();
			while (clazz != null && clazz != Object.class) {
				for (Field field : clazz.getDeclaredFields()) {
					fields.add(field);
				}
				clazz = clazz.getSuperclass();
			}
			return fields;
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

	private class CastingMatcher<S, T> extends TypeSafeMatcher<S> {

		private Class<S> clazz;
		private Matcher<T> matcher;

		public CastingMatcher(Class<S> clazz, Matcher<T> matcher) {
			this.clazz = clazz;
			this.matcher = matcher;
		}

		@Override
		public void describeTo(Description description) {
			description.appendDescriptionOf(matcher);
		}

		@Override
		protected boolean matchesSafely(S item) {
			if (!clazz.isInstance(item)) {
				return false;
			}
			if (!matcher.matches(item)) {
				return false;
			}
			return true;
		}

	}

}
