package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Method;

public class MethodsByName implements Methods {

	private String name;

	public MethodsByName(String name) {
		this.name = name;
	}

	@Override
	public boolean matches(Method method) {
		return method.getName().equals(name);
	}

	@Override
	public boolean matches(String className, String methodName, String methodDescriptor) {
		return methodName.equals(name);
	}

}
