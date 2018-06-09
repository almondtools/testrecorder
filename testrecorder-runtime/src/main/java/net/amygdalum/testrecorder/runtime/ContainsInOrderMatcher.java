package net.amygdalum.testrecorder.runtime;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsNull;

public class ContainsInOrderMatcher<T> extends AbstractIterableMatcher<T, Collection<? extends T>> {

	private Class<T> type;
	private List<Matcher<T>> elements;

	public ContainsInOrderMatcher(Class<T> type) {
		this.type = type;
		this.elements = new ArrayList<>();
	}

	public ContainsInOrderMatcher<T> element(T element) {
		return element(match(element));
	}

	public ContainsInOrderMatcher<T> element(Matcher<T> element) {
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
		description.appendText("containing in sequence ").appendValueList("[", ", ", "]", elements);
	}

	@Override
	protected void describeMismatchSafely(Collection<? extends T> item, Description mismatchDescription) {
		Matches<T> matches = new Matches<>();

		Iterator<Matcher<T>> elementIterator = elements.iterator();
		Iterator<? extends T> itemIterator = item.iterator();
		while (elementIterator.hasNext() && itemIterator.hasNext()) {
			Matcher<T> matcher = elementIterator.next();
			T element = itemIterator.next();
			if (!matcher.matches(element)) {
				matches.mismatch(matcher, element);
			} else {
				matches.match();
			}
		}
		if (elementIterator.hasNext()) {
			List<Matcher<T>> matchers = remainder(elementIterator);
			matches.mismatch("missing " + matchers.size() + " elements " + toExpectedSet(matchers));
		}
		if (itemIterator.hasNext()) {
			List<T> items = remainder(itemIterator);
			matches.mismatch("found " + items.size() + " elements surplus " + toFoundSet(items));
		}

		if (matches.containsMismatches()) {
			mismatchDescription.appendText("mismatching elements ").appendDescriptionOf(matches);
		}
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
		Iterator<Matcher<T>> elementIterator = elements.iterator();
		Iterator<? extends T> itemIterator = item.iterator();
		while (elementIterator.hasNext() && itemIterator.hasNext()) {
			Matcher<T> matcher = elementIterator.next();
			T element = itemIterator.next();
			if (!matcher.matches(element)) {
				return false;
			}
		}
		return !elementIterator.hasNext()
			&& !itemIterator.hasNext();
	}

	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> ContainsInOrderMatcher<T> containsInOrder(Class<T> key, Object... elements) {
		ContainsInOrderMatcher<T> set = new ContainsInOrderMatcher<>(key);
		for (Object element : elements) {
			if (element instanceof Matcher) {
				set.element((Matcher<T>) element);
			} else {
				set.element(key.cast(element));
			}
		}
		return set;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SafeVarargs
	public static ContainsInOrderMatcher containsInOrder(Object... elements) {
		ContainsInOrderMatcher set = new ContainsInOrderMatcher(Object.class);
		for (Object element : elements) {
			if (element instanceof Matcher) {
				set.element((Matcher) element);
			} else {
				set.element(element);
			}
		}
		return set;
	}
}
