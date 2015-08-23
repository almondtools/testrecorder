package com.almondtools.invivoderived;

import java.lang.reflect.Type;

public class GeneratedSnapshotFactory {

	private Type resultType;
	private String methodName;
	private Type[] argumentTypes;

	
	public GeneratedSnapshotFactory(Type resultType, String methodName, Type... argumentTypes) {
		this.resultType = resultType;
		this.methodName = methodName;
		this.argumentTypes = argumentTypes;
	}

	public GeneratedSnapshot create() {
		return new GeneratedSnapshot(resultType, methodName, argumentTypes);
	}

}
