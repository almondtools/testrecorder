package com.almondtools.invivoderived;

public class GeneratedSnapshot {

	private SerializerFacade setupFacade;
	private SerializerFacade expectFacade;

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
		this.setupFacade = new SerializerFacade();
		this.expectFacade = new SerializerFacade();
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

	public SerializedValue[] getSetupArgs() {
		return setupArgs;
	}

	public SerializedValue getExpectThis() {
		return expectThis;
	}

	public SerializedValue getExpectResult() {
		return expectResult;
	}

	public SerializedValue getExpectException() {
		return expectException;
	}

	public SerializedValue[] getExpectArgs() {
		return expectArgs;
	}

	public void setupThis(Object self) {
		this.setupThis = setupFacade.serialize(self.getClass(), self);
	}

	public void setupArgs(Object[] args) {
		this.setupArgs = setupFacade.serialize(argumentTypes, args);
	}

	public void expectThis(Object self) {
		this.expectThis = expectFacade.serialize(self.getClass(), self);
	}

	public void expectArgs(Object[] args) {
		this.expectArgs = expectFacade.serialize(argumentTypes, args);
	}

	public void expectResult(Object result) {
		this.expectResult = expectFacade.serialize(resultType, result);
	}

	public void expectException(Throwable throwable) {
		this.expectException = expectFacade.serialize(throwable.getClass(), throwable);
	}

}
