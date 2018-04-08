package net.amygdalum.testrecorder.runtime;

import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

public class Matches<T> implements SelfDescribing {

	private Deque<Matching<T>> matches;
	private boolean mismatches;

	public Matches() {
		this.matches = new LinkedList<>();
		this.mismatches = false;
	}

	public Matches<T> mismatch(Matcher<T> matcher, Object element) {
		matches.add(new Mismatch<>(matcher, element));
		mismatches = true;
		return this;
	}

	public Matches<T> mismatch(String description) {
		matches.add(new MismatchDescription<T>(description));
		mismatches = true;
		return this;
	}

	public Matches<T> match() {
		Matching<T>  peek = matches.peek();
		if (peek instanceof Match<?>) {
			((Match<?>) peek).inc();;
		} else {
			matches.add(new Match<T>());
		}
		return this;
	}

	public boolean containsMismatches() {
		return mismatches;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("<[");
		Iterator<Matching<T>> matchIterator = matches.iterator();
		if (matchIterator.hasNext()) {
			description.appendDescriptionOf(matchIterator.next());
		}
		while (matchIterator.hasNext()) {
			description.appendText(", ");
			description.appendDescriptionOf(matchIterator.next());
		}
		description.appendText("]>");
	}
	
	private static abstract class Matching<T> implements SelfDescribing {

	}

	private static class Match<T> extends Matching<T> {
		
		private int count;

		public Match() {
			this.count = 1;
		}
		
		public void inc() {
			count++;
		}

		@Override
		public void describeTo(Description description) {
			char[] text = new char[count];
			Arrays.fill(text, '.');
			description.appendText(new String(text));
		}
	}

	private static class MismatchDescription<T> extends Matching<T> {

		private String description;

		public MismatchDescription(String description) {
			this.description = description;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText(this.description);
		}
}

	private static class Mismatch<T> extends Matching<T> {

		private Matcher<T> matcher;
		private Object element;

		public Mismatch(Matcher<T> matcher, Object element) {
			this.matcher = matcher;
			this.element = element;
		}

		@Override
		public void describeTo(Description description) {
			matcher.describeMismatch(element, description);
		}
	}

}
