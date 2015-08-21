package com.almondtools.invivoderived;

public class GeneratedSnapshot {

	private Class<?> resultType;
	private String methodName;
	private Class<?>[] argumentTypes;

	private SerializedValue setupThis;
	private SerializedValue[] setupArgs;

	private SerializedValue expectThis;
	private SerializedValue expectResult;
	private SerializedValue expectException;
	private SerializedValue[] expectArgs;

	public GeneratedSnapshot(Class<?> resultType, String methodName, Class<?>... argumentTypes) {
		this.resultType = resultType;
		this.methodName = methodName;
		this.argumentTypes = argumentTypes;
	}

	public Class<?> getResultType() {
		return resultType;
	}

	public String getMethodName() {
		return methodName;
	}

	public Class<?>[] getArgumentTypes() {
		return argumentTypes;
	}

	public SerializedValue getSetupThis() {
		return setupThis;
	}
	
	public void setSetupThis(SerializedValue setupThis) {
		this.setupThis = setupThis;
	}

	public SerializedValue[] getSetupArgs() {
		return setupArgs;
	}
	
	public void setSetupArgs(SerializedValue[] setupArgs) {
		this.setupArgs = setupArgs;
	}

	public SerializedValue getExpectThis() {
		return expectThis;
	}
	
	public void setExpectThis(SerializedValue expectThis) {
		this.expectThis = expectThis;
	}

	public SerializedValue getExpectResult() {
		return expectResult;
	}
	
	public void setExpectResult(SerializedValue expectResult) {
		this.expectResult = expectResult;
	}

	public SerializedValue getExpectException() {
		return expectException;
	}
	
	public void setExpectException(SerializedValue expectException) {
		this.expectException = expectException;
	}

	public SerializedValue[] getExpectArgs() {
		return expectArgs;
	}
	
	public void setExpectArgs(SerializedValue[] expectArgs) {
		this.expectArgs = expectArgs;
	}

}
