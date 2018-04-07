package net.amygdalum.testrecorder.scenarios;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import net.amygdalum.testrecorder.profile.Recorded;

@SuppressWarnings("unused")
public class DifferentPublicDeclarationTypes {

	private MyEnum myEnum = MyEnum.VALUE2;
	private MyClass myClass;
	private MyInterface myInterface;

	@Recorded
	public void test() {
		myEnum = MyEnum.VALUE2;
		myClass = new MyClass();
		myInterface = new MyClass();
	}

	public static enum MyEnum {
		VALUE1, VALUE2;
	}

	@MyAnnotation
	public static class MyClass implements MyInterface {
	}

	public interface MyInterface {
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface MyAnnotation {

	}
}
