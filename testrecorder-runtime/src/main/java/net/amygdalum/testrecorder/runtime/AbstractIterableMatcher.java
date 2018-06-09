package net.amygdalum.testrecorder.runtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.hamcrest.TypeSafeMatcher;

public abstract class AbstractIterableMatcher<S, T> extends TypeSafeMatcher<T> {

	protected abstract Matcher<S> bestMatcher();

	protected <R> List<R> remainder(Iterator<? extends R> iterator) {
		List<R> remainder = new ArrayList<>();
		while (iterator.hasNext()) {
			remainder.add(iterator.next());
		}
		return remainder;
	}

	protected Set<String> toFoundSet(List<? extends S> elements) {
		Matcher<S> matcher = bestMatcher();
		Set<String> set = new LinkedHashSet<>();
		for (S element : elements) {
			String desc = descriptionOf(matcher, element);
			set.add(desc);
		}
		return set;
	}

	protected Set<String> toExpectedSet(List<Matcher<S>> matchers) {
		Set<String> set = new LinkedHashSet<>();
		for (Matcher<S> matcher : matchers) {
			String desc = descriptionOf(matcher);
			set.add(desc);
		}
		return set;
	}

	protected <R> String descriptionOf(Matcher<R> matcher) {
		StringDescription description = new StringDescription();
		matcher.describeTo(description);
		return description.toString();
	}

	protected <R> String descriptionOf(Matcher<R> matcher, R value) {
		StringDescription description = new StringDescription();
		matcher.describeMismatch(value, description);
		return description.toString();
	}

}
