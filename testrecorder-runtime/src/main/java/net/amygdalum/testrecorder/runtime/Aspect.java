package net.amygdalum.testrecorder.runtime;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.objectweb.asm.Type;


public class Aspect {

	private Method method;
	
	public Aspect() {
		Method[] methods = Arrays.stream(getClass().getDeclaredMethods())
			.filter(method -> !method.isSynthetic())
			.toArray(Method[]::new);
		if (methods.length != 1) {
			
			throw new IllegalArgumentException("faked aspects must contain exactly one method, but found: " + Arrays.toString(methods)); 
		}
		method = methods[0];
	}

	public String getName() {
		return method.getName();
	}

	public String getDesc() {
		return Type.getMethodDescriptor(method);
	}

}
