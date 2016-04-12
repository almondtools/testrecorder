package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class ParameterizedTypeMatcher extends TypeSafeDiagnosingMatcher<Type> {

	private Class<?> raw;
	private Matcher<Type>[] args;

	public ParameterizedTypeMatcher(Class<?> raw, Matcher<Type>[] args) {
		this.raw = raw;
		this.args = args;
	}

	@Override
	protected boolean matchesSafely(Type item, Description mismatchDescription) {
		if (!(item instanceof ParameterizedType)) {
			mismatchDescription.appendText("type was not parameterized");
			return false;
		}
		ParameterizedType parameterizedType = (ParameterizedType) item;
		Type rawType = parameterizedType.getRawType();
		if (!rawType.equals(raw)) {
			mismatchDescription.appendText("raw type was ").appendValue(rawType);
			return false;
		}

		Type[] typeArgsItem = parameterizedType.getActualTypeArguments();
		if (typeArgsItem.length != args.length) {
			mismatchDescription.appendText("type argument number not matching: ").appendValue(typeArgsItem);
			return false;
		}
		for (int i = 0; i < typeArgsItem.length; i++) {
			if (!args[i].matches(typeArgsItem[i])) {
				mismatchDescription.appendText("type argument " + i + " not matching: ").appendValue(typeArgsItem[i]);
				return false;
			}
		}

		return true;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("a parameterized type\n");
		description.appendText("  with raw type ").appendValue(raw).appendText("\n");
		description.appendText("  and type arguments ").appendList("", ",", "", asList(args));
	}

	public static ParameterizedTypeMatcher parameterizedType(Class<?> raw, Class<?>... args) {
		return new ParameterizedTypeMatcher(raw, toMatchers(args));
	}

	@SuppressWarnings("unchecked")
	private static Matcher<Type>[] toMatchers(Class<?>[] args) {
		Matcher<Type>[] types = new Matcher[args.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = equalTo(args[i]);
		}
		return types;
	}

}
