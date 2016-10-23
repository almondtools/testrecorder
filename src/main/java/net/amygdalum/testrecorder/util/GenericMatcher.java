package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.GenericComparatorResult.MATCH;
import static net.amygdalum.testrecorder.util.GenericComparatorResult.MISMATCH;
import static net.amygdalum.testrecorder.util.GenericComparatorResult.NOT_APPLYING;
import static net.amygdalum.testrecorder.util.GenericComparison.getValue;
import static net.amygdalum.testrecorder.util.Types.allFields;
import static org.hamcrest.Matchers.instanceOf;

import java.lang.reflect.Field;
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

	public List<GenericComparison> mismatchesWith(String root, Object o) {
		WorkSet<GenericComparison> remainder = new WorkSet<>();
		for (Field field : getGenericFields()) {
			remainder.enqueue(GenericComparison.from(root, field.getName(), this, o));
		}
		GenericComparison.compare(remainder, GenericMatcher::matching);
		return remainder.getDone().stream()
			.filter(done -> done.isMismatch())
			.collect(toList());
	}

	private static GenericComparatorResult matching(GenericComparison comparison, WorkSet<GenericComparison> todo) {
		Object left = comparison.getLeft();
		Object right = comparison.getRight();
		if (left instanceof RecursiveMatcher) {
			RecursiveMatcher matcher = (RecursiveMatcher) left;
			todo.enqueue(matcher.mismatchesWith(comparison.getRoot(), right));
		}
		if (left instanceof Matcher<?>) {
			Matcher<?> matcher = (Matcher<?>) left;
			if (matcher.matches(right)) {
				return MATCH;
			} else {
				return MISMATCH;
			}
		}
		return NOT_APPLYING;
	}

	public static <T> Matcher<T> recursive(Class<T> clazz) {
		return instanceOf(clazz);
	}

	public static Matcher<?> recursive(Wrapped wrapped) {
		return instanceOf(wrapped.getWrappedClass());
	}

	private class InternalsMatcher<T> extends TypeSafeMatcher<T> implements RecursiveMatcher {

		private Class<T> clazz;

		public InternalsMatcher(Class<T> clazz) {
			this.clazz = clazz;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("\n").appendText(clazz.getName()).appendText(" {");
			for (Field field : allFields(clazz)) {
				describeField(description, field, GenericMatcher.this);
			}
			description.appendText("\n}");
		}

		@Override
		public List<GenericComparison> mismatchesWith(String root, Object item) {
			return GenericMatcher.this.mismatchesWith(root, item);
		}

		@Override
		protected boolean matchesSafely(T item) {
			return clazz == item.getClass()
				&& mismatchesWith(null, item).isEmpty();
		}

		@Override
		protected void describeMismatchSafely(T item, Description mismatchDescription) {
			List<GenericComparison> mismatches = mismatchesWith(null, item);
			if (!mismatches.isEmpty()) {
				if (item == null) {
					mismatchDescription.appendText("is").appendValue(null);
				} else {
					mismatchDescription.appendText("\n").appendText(item.getClass().getName()).appendText(" {");
					for (Field field : allFields(item.getClass())) {
						describeField(mismatchDescription, field, item);
					}
					mismatchDescription.appendText("\n}");
				}
				mismatchDescription.appendText("\nfound mismatches at:");
				for (GenericComparison mismatch : mismatches) {
					if (!(mismatch.getLeft() instanceof RecursiveMatcher)) {
						describeMismatch(mismatchDescription, mismatch);
					}
				}
			}
		}

		private void describeField(Description description, Field field, Object object) {
			try {
				description.appendText("\n\t")
					.appendText(field.getType().getSimpleName()).appendText(" ")
					.appendText(field.getName()).appendText(": ");
				Object value = getValue(field.getName(), object);
				describe(description, value);
				description.appendText(";");
			} catch (ReflectiveOperationException e) {
				description.appendText("\n\t")
					.appendValue(field.getType()).appendText(" ")
					.appendValue(field.getName()).appendText(":<description failed>");
			}
		}

		private void describeMismatch(Description description, GenericComparison mismatch) {
			description.appendText("\n\t")
				.appendText(mismatch.getRoot()).appendText(": ");
			describe(description, mismatch.getLeft());
			description.appendText(" != ");
			describe(description, mismatch.getRight());
		}

		public void describe(Description description, Object value) {
			if (value instanceof SelfDescribing) {
				description.appendDescriptionOf((SelfDescribing) value);
			} else {
				description.appendValue(value);
			}
		}

	}

	private class CastingMatcher<S, T> extends TypeSafeMatcher<S> implements RecursiveMatcher {

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
		public List<GenericComparison> mismatchesWith(String root, Object item) {
			return GenericMatcher.this.mismatchesWith(root, item);
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
