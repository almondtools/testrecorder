package net.amygdalum.testrecorder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public class ContextSnapshotFactory {

	private SerializationProfile profile;
	private Class<?> declaringClass;
	private Type resultType;
	private Annotation[] resultAnnotation;
	private String methodName;
	private Type[] argumentTypes;
    private Annotation[][] argumentAnnotations;

	public ContextSnapshotFactory(SerializationProfile profile, Method method) {
        this.profile = profile;
        this.declaringClass = method.getDeclaringClass();
        this.resultType = method.getGenericReturnType();
        this.resultAnnotation = method.getAnnotations();
        this.methodName = method.getName();
        this.argumentTypes = method.getGenericParameterTypes();
        this.argumentAnnotations = method.getParameterAnnotations();
    }

    public SerializationProfile profile() {
		return profile;
	}

	public List<Field> getGlobalFields() {
		return profile.getGlobalFields();
	}

	public ContextSnapshot createSnapshot() {
		return new ContextSnapshot(System.currentTimeMillis(), declaringClass, resultAnnotation, resultType, methodName, argumentAnnotations, argumentTypes);
	}

}
