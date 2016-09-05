package net.amygdalum.testrecorder.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsNull;

public class ContainsMatcher<T> extends TypeSafeMatcher<Collection<? extends T>> {

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
		description.appendValue(elements);
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
			matches.mismatch("found " + notExpected.size() + " elements surplus " + toDescriptionSet(notExpected));
		}
		if (!unmatched.isEmpty()) {
			matches.mismatch("missing " + unmatched.size() + " elements");
		}
		mismatchDescription.appendText("mismatching elements ").appendDescriptionOf(matches);
	}

	private Set<String> toDescriptionSet(List<T> elements) {
		Matcher<T> matcher = bestMatcher();
		Set<String> set = new LinkedHashSet<>();
		for (T element : elements) {
			String desc = descriptionOf(matcher, element);
			set.add(desc);
		}
		return set;
	}

	private Matcher<T> bestMatcher() {
		for (Matcher<T> matcher : elements) {
			if (matcher.getClass() != IsNull.class) {
				return matcher;
			}
		}
		return equalTo(null);
	}

	private <S> String descriptionOf(Matcher<S> matcher, S value) {
		StringDescription description = new StringDescription();
		matcher.describeMismatch(value, description);
		return description.toString();
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

}
