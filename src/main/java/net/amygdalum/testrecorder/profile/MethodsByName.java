package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.objectweb.asm.Type;

public class MethodsByName implements Methods {

	private static final Pattern NAME = Pattern.compile("[\\w$.]+");
	
	private String clazz;
	private String name;

	public MethodsByName(String name) {
		if (!NAME.matcher(name).matches()) {
			throw new IllegalArgumentException("method name should contain only word characters, dot and $, but was: " + name);
		}
		int lastDot = name.lastIndexOf('.');
		if (lastDot > -1) {
			this.clazz = name.substring(0, lastDot);
			this.name = name.substring(lastDot + 1);
		} else {
			this.name = name;
		}
	}

	@Override
	public boolean matches(Method method) {
		if (clazz == null) {
			return method.getName().equals(name);
		}
		return method.getName().equals(name)
			&& method.getDeclaringClass().getName().equals(clazz);
	}

	@Override
	public boolean matches(String className, String methodName, String methodDescriptor) {
		if (clazz == null) {
			return methodName.equals(name);
		}
		String refName = Type.getObjectType(className).getClassName();
		return methodName.equals(name)
			&& refName.equals(clazz);
	}

}
