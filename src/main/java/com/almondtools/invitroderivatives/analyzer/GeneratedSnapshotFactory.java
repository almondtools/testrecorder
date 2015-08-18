package com.almondtools.invitroderivatives.analyzer;

import com.almondtools.invitroderivatives.GeneratedSnapshot;

public class GeneratedSnapshotFactory {

	private Class<?> resultType;
	private String methodName;
	private Class<?>[] argumentTypes;

	
	public GeneratedSnapshotFactory(Class<?> resultType, String methodName, Class<?>... argumentTypes) {
		this.resultType = resultType;
		this.methodName = methodName;
		this.argumentTypes = argumentTypes;
	}

	public GeneratedSnapshot create() {
		return new GeneratedSnapshot(resultType, methodName, argumentTypes);
	}

}
