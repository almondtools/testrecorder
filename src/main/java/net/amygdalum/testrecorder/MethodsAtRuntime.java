package net.amygdalum.testrecorder;

public interface MethodsAtRuntime extends Methods {

	default boolean matches(String className, String methodName, String methodDescriptor) {
		return false;
	}

}
