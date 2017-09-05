package net.amygdalum.testrecorder;

public interface MethodsAtRuntime {

	default boolean matches(String className, String methodDescriptor) {
		return false;
	}

}
