package com.almondtools.testrecorder;

import java.lang.reflect.Type;

import com.almondtools.testrecorder.profile.DefaultSerializationProfile;

public class ContextSnapshotFactory {

	private SerializationProfile profile;
	private Class<?> declaringClass;
	private Type resultType;
	private String methodName;
	private Type[] argumentTypes;

	public ContextSnapshotFactory(Class<?> declaringClass, Snapshot snapshot, Type resultType, String methodName, Type... argumentTypes) {
		this.declaringClass = declaringClass;
		this.profile = instantiate(snapshot.profile());
		this.resultType = resultType;
		this.methodName = methodName;
		this.argumentTypes = argumentTypes;
	}

	private SerializationProfile instantiate(Class<? extends SerializationProfile> profile) {
		try {
			return profile.newInstance();
		} catch (InstantiationException | IllegalAccessException | NullPointerException e) {
			return new DefaultSerializationProfile();
		}
	}
	
	public SerializationProfile profile() {
		return profile;
	}

	public ContextSnapshot createSnapshot() {
		return new ContextSnapshot(declaringClass, resultType, methodName, argumentTypes);
	}

}
