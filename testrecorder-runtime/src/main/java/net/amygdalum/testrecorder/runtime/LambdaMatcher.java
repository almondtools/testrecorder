package net.amygdalum.testrecorder.runtime;

import java.lang.invoke.SerializedLambda;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import net.amygdalum.testrecorder.util.Lambdas;

public class LambdaMatcher extends BaseMatcher<Object> {

	private String name;

	public LambdaMatcher(String name) {
		this.name = name;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("with implementation ").appendValue(name);
	}

	@Override
	public boolean matches(Object item) {
		if (!Lambdas.isSerializableLambda(item.getClass())) {
			return false;
		}
		SerializedLambda lambda = Lambdas.serializeLambda(item);
		return lambda.getImplMethodName().equals(name);
	}

	public static LambdaMatcher lambda(String name) {
		return new LambdaMatcher(name);
	}

}