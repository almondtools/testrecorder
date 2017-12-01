package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.profile.Methods;

public interface MethodsAtRuntime extends Methods {

	default boolean matches(String className, String methodName, String methodDescriptor) {
		return false;
	}

}
