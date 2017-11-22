package net.amygdalum.testrecorder.runtime;

public class Invocation {

	public String callerClassName;
	public String callerMethodName;
	public int callerLine;
	public Object instance;
	public Class<?> clazz;
	public String methodName;
	public String methodDesc;
	
	private Invocation(String callerClassName, String callerMethodName, int callerLine, Object instance, Class<?> clazz, String methodName, String methodDesc) {
		this.callerClassName = callerClassName;
		this.callerMethodName = callerMethodName;
		this.callerLine = callerLine;
		this.instance = instance;
		this.clazz = clazz;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
	}
	
	public static Invocation capture(StackTraceElement[] stackTrace, Object instance, Class<?> clazz, String methodName, String methodDesc) {
		StackTraceElement stack = stackTrace[1];
		return new Invocation(stack.getClassName(), stack.getMethodName(), stack.getLineNumber(), instance, clazz, methodName, methodDesc);
	}

	public String getCallee() {
		Class<?> calleeClass = instance != null ? instance.getClass() : clazz; 
		return calleeClass.getName() + "." + methodName + methodDesc;
	}
	
	public String getCaller() {
		return callerClassName + "." + callerMethodName + ":" + callerLine;
	}

}