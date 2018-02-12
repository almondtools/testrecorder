package net.amygdalum.testrecorder.runtime;

public class Invocation {

	public Object instance;
	public Class<?> clazz;
	public String methodName;
	public String methodDesc;
	
	private Invocation(Object instance, Class<?> clazz, String methodName, String methodDesc) {
		this.instance = instance;
		this.clazz = clazz;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
	}
	
	public static Invocation capture(Object instance, Class<?> clazz, String methodName, String methodDesc) {
		return new Invocation(instance, clazz, methodName, methodDesc);
	}

}