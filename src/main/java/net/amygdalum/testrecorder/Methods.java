package net.amygdalum.testrecorder;

import java.lang.reflect.Method;

import net.amygdalum.testrecorder.profile.MethodDescription;
import net.amygdalum.testrecorder.profile.MethodsByName;

public interface Methods {

	boolean matches(Method method);

	boolean matches(String className, String methodName, String methodDescriptor);

	static Methods byDescription(String className, String methodName, String methodDescriptor) {
		return new MethodDescription(className, methodName, methodDescriptor);
	}

	static Methods byName(String name) {
		return new MethodsByName(name);
	}

}
