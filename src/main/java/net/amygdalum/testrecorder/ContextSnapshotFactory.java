package net.amygdalum.testrecorder;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

public class ContextSnapshotFactory {

	private SerializationProfile profile;
	private Class<?> declaringClass;
	private Type resultType;
	private String methodName;
	private Type[] argumentTypes;

	public ContextSnapshotFactory(Class<?> declaringClass, SerializationProfile profile, Type resultType, String methodName, Type... argumentTypes) {
		this.declaringClass = declaringClass;
		this.profile = profile;
		this.resultType = resultType;
		this.methodName = methodName;
		this.argumentTypes = argumentTypes;
	}

	public SerializationProfile profile() {
		return profile;
	}

	public List<Field> getGlobalFields() {
		return profile.getGlobalFields();
	}

	public ContextSnapshot createSnapshot() {
		return new ContextSnapshot(declaringClass, resultType, methodName, argumentTypes);
	}

}
