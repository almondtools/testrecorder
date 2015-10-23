package com.almondtools.testrecorder;

import java.lang.reflect.Type;

import com.almondtools.testrecorder.profile.DefaultSerializationProfile;

public class ValueSnapshotFactory {

	private SerializationProfile profile;
	private Class<?> declaringClass;
	private Type type;
	private String fieldName;

	public ValueSnapshotFactory(Class<?> declaringClass, Snapshot snapshot, Type type, String fieldName) {
		this.declaringClass = declaringClass;
		this.profile = instantiate(snapshot.profile());
		this.type = type;
		this.fieldName = fieldName;
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

	public ValueSnapshot createSnapshot() {
		return new ValueSnapshot(declaringClass, type, fieldName);
	}

}
