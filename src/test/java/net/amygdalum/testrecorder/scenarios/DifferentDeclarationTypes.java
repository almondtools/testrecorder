package net.amygdalum.testrecorder.scenarios;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import net.amygdalum.testrecorder.Snapshot;

@SuppressWarnings("unused")
public class DifferentDeclarationTypes {

	private MyEnum myEnum = MyEnum.VALUE1;
	private MyClass myClass;
	private MyInterface myInterface;
	private MyExtendedEnum myExtendedEnum = MyExtendedEnum.VALUE1;

	@Snapshot
	public void test() {
		myEnum = MyEnum.VALUE2;
		myClass = new MyClass();
		myInterface = new MyClass();
		myExtendedEnum = MyExtendedEnum.VALUE2;
	}

}

enum MyEnum {
	VALUE1, VALUE2;
}

enum MyExtendedEnum {
	VALUE1 {
	},
	VALUE2 {
	};
}

@MyAnnotation
class MyClass implements MyInterface {
}

interface MyInterface {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MyAnnotation {

}