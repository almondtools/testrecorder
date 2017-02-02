package net.amygdalum.testrecorder.util;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class EnumMatcher extends TypeSafeMatcher<Enum<?>> {

	private String name;

	public EnumMatcher(String name) {
		this.name = name;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("with name ").appendValue(name);
	}

	@Override
	protected boolean matchesSafely(Enum<?> item) {
		return name.equals(item.name());
	}

	public static EnumMatcher matchingEnum(String name) {
		return new EnumMatcher(name);
	}

}
