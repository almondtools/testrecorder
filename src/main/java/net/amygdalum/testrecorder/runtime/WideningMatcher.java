package net.amygdalum.testrecorder.runtime;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class WideningMatcher extends BaseMatcher<Object> {

	private Matcher<?> wrapped;

	public WideningMatcher(Matcher<?> wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public void describeTo(Description description) {
		wrapped.describeTo(description);
	}

	@Override
	public boolean matches(Object item) {
		return wrapped.matches(item);
	}

	public static WideningMatcher widening(Matcher<?> wrapped) {
		return new WideningMatcher(wrapped);
	}

}
