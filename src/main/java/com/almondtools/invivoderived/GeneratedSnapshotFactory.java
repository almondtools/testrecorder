package com.almondtools.invivoderived;

import java.lang.reflect.Type;

import com.almondtools.invivoderived.profile.DefaultSerializationProfile;

public class GeneratedSnapshotFactory {

	private SerializationProfile profile;
	private Type resultType;
	private String methodName;
	private Type[] argumentTypes;

	public GeneratedSnapshotFactory(Snapshot snapshot, Type resultType, String methodName, Type... argumentTypes) {
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

	public GeneratedSnapshot create() {
		return new GeneratedSnapshot(resultType, methodName, argumentTypes);
	}

}
