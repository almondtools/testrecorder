package net.amygdalum.testrecorder.runtime;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsNull;

public class ContainsMatcher<T> extends AbstractIterableMatcher<T, Collection<? extends T>> {

	private Class<T> type;
	private List<Matcher<T>> elements;

	public ContainsMatcher(Class<T> type) {
		this.type = type;
		this.elements = new ArrayList<>();
	}

	public ContainsMatcher<T> and(T element) {
		return and(match(element));
	}

	public ContainsMatcher<T> and(Matcher<T> element) {
		elements.add(element);
		return this;
	}

	private Matcher<T> match(T element) {
		if (element == null) {
			return nullValue(type);
		} else {
			return equalTo(element);
		}
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("containing ").appendValueList("[", ", ", "]", elements);
	}

	@Override
	protected void describeMismatchSafely(Collection<? extends T> item, Description mismatchDescription) {
		List<Matcher<T>> unmatched = new LinkedList<>(elements);
		Matches<T> matches = new Matches<>();
		List<T> notExpected = new ArrayList<>();

		for (T element : item) {

			boolean success = tryMatch(unmatched, element);
			if (success) {
				matches.match();
			} else {
				notExpected.add(element);
			}
		}

		if (!notExpected.isEmpty()) {
			matches.mismatch("found " + notExpected.size() + " elements surplus " + toFoundSet(notExpected));
		}
		if (!unmatched.isEmpty()) {
			matches.mismatch("missing " + unmatched.size() + " elements " + toExpectedSet(unmatched));
		}
		mismatchDescription.appendText("mismatching elements ").appendDescriptionOf(matches);
	}

	@Override
	protected Matcher<T> bestMatcher() {
		for (Matcher<T> matcher : elements) {
			if (matcher.getClass() != IsNull.class) {
				return matcher;
			}
		}
		return equalTo(null);
	}

	@Override
	protected boolean matchesSafely(Collection<? extends T> item) {
		List<Matcher<T>> unmatched = new LinkedList<>(elements);

		for (T element : item) {
			boolean success = tryMatch(unmatched, element);
			if (!success) {
				return false;
			}
		}

		return unmatched.isEmpty();
	}

	private boolean tryMatch(List<Matcher<T>> unmatched, T element) {
		Iterator<Matcher<T>> matchers = unmatched.iterator();
		while (matchers.hasNext()) {
			Matcher<T> matcher = matchers.next();
			if (matcher.matches(element)) {
				matchers.remove();
				return true;
			}
		}
		return false;
	}

	public static <T> ContainsMatcher<T> empty(Class<T> type) {
		return new ContainsMatcher<>(type);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ContainsMatcher empty() {
		return new ContainsMatcher(Object.class);
	}

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> ContainsMatcher<T> contains(Class<T> key, Object... elements) {
		ContainsMatcher<T> set = new ContainsMatcher<>(key);
		for (Object element : elements) {
			if (element instanceof Matcher) {
				set.and((Matcher<T>) element);
			} else {
				set.and(key.cast(element));
			}
		}
		return set;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SafeVarargs
	public static ContainsMatcher contains(Object... elements) {
		ContainsMatcher set = new ContainsMatcher(Object.class);
		for (Object element : elements) {
			if (element instanceof Matcher) {
				set.and((Matcher) element);
			} else {
				set.and(element);
			}
		}
		return set;
	}

}
