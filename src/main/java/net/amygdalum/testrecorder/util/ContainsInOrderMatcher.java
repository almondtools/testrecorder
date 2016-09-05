package net.amygdalum.testrecorder.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsNull;

public class ContainsInOrderMatcher<T> extends TypeSafeMatcher<Collection<? extends T>> {

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
		description.appendValue(elements);
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
			int count = count(elementIterator);
			matches.mismatch("missing " + count + " elements");
		}
		if (itemIterator.hasNext()) {
			List<T> items = collect(itemIterator);
			matches.mismatch("found " + items.size() + " elements surplus " + toDescriptionSet(items));
		}
		
		if (matches.containsMismatches()) {
			mismatchDescription.appendText("mismatching elements ").appendDescriptionOf(matches);
		}
	}

	private int count(Iterator<?> iterator) {
		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			count++;
		}
		return count;
	}

	private List<T> collect(Iterator<? extends T> iterator) {
		List<T> collected = new ArrayList<>();
		while (iterator.hasNext()) {
			collected.add(iterator.next());
		}
		return collected;
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
		if (item.size() != elements.size()) {
			return false;
		}

		Iterator<Matcher<T>> elementIterator = elements.iterator();
		Iterator<? extends T> itemIterator = item.iterator();
		while (elementIterator.hasNext() && itemIterator.hasNext()) {
			Matcher<T> matcher = elementIterator.next();
			T element = itemIterator.next();
			if (!matcher.matches(element)) {
				return false;
			}
		}
		return true;
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

}
