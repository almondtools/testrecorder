package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Method;

import org.objectweb.asm.Type;

public class MethodDescription implements Methods {

	private String className;
	private String methodName;
	private String methodDescriptor;

	public MethodDescription(String className, String methodName, String methodDescriptor) {
		this.className = className;
		this.methodName = methodName;
		this.methodDescriptor = methodDescriptor;
	}

	@Override
	public boolean matches(Method method) {
		String className = Type.getInternalName(method.getDeclaringClass());
		String methodName = method.getName();
		String methodDescriptor = Type.getMethodDescriptor(method);
		return this.className.equals(className)
			&& this.methodName.equals(methodName)
			&& this.methodDescriptor.equals(methodDescriptor);
	}

	@Override
	public boolean matches(String className, String methodName, String methodDescriptor) {
		return this.className.equals(className)
			&& this.methodName.equals(methodName)
			&& this.methodDescriptor.equals(methodDescriptor);
	}

}
