package com.almondtools.util.objects;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class EqualityMatcher<T> extends TypeSafeDiagnosingMatcher<T> {

	private static final Object differentObject = new Object();
	
	private List<T> equals;
	private List<T> notEquals;

	public EqualityMatcher() {
		this.equals = new ArrayList<>();
		this.notEquals = new ArrayList<>();
	}
	
	public static <T> EqualityMatcher<T> satisfiesDefaultEquality() {
		return new EqualityMatcher<T>();
	}
	
	public EqualityMatcher<T> andEqualTo(T element) {
		this.equals.add(element);
		return this;
	}

	public EqualityMatcher<T> andNotEqualTo(T element) {
		this.notEquals.add(element);
		return this;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("should satisfy common equality contraints as\n");
		description.appendText("- object should not equal null or object of a different class\n");
		description.appendText("- object should equal itself\n");
		description.appendText("and special contrains given:");
		for (T element : equals) {
			description.appendText("- should equal ").appendValue(element).appendText("\n");
		}
		for (T element : notEquals) {
			description.appendText("- should not equal ").appendValue(element).appendText("\n");
		}
	}

	@Override
	protected boolean matchesSafely(T item, Description mismatchDescription) {
		if (item == null) {
			mismatchDescription.appendText("is null and not equal to any object");
			return false;
		}
		
		if (!item.equals(item) || item.hashCode() != item.hashCode()) {
			mismatchDescription.appendText("should equal self");
			return false;
		}
		for (T element : equals) {
			if (!item.equals(element) || !element.equals(item) || item.hashCode() != element.hashCode()) {
				mismatchDescription.appendText("should equal ").appendValue(element).appendText(", was ").appendValue(item);
				return false;
			}
		}
		
		if (item.equals(null)) {
			mismatchDescription.appendText("should not equal null");
			return false;
		}
		if (item.equals(differentObject)) {
			mismatchDescription.appendText("should not equal a foreign object");
			return false;
		}
		for (T element : notEquals) {
			if (item.equals(element) || element.equals(item)) {
				mismatchDescription.appendText("should not equal ").appendValue(element).appendText(", was ").appendValue(item);
				return false;
			}
		}
		return true;
	}

}
