package net.amygdalum.testrecorder.types;

public class AnalyzedObject {
	public Object object;
	public Class<?> effectiveType;
	public Object effectiveObject;

	public AnalyzedObject(Object object) {
		this(object, object == null ? null : object.getClass(), object);
	}

	public AnalyzedObject(Class<?> effectiveType, Object object) {
		this(object, effectiveType, object);
	}

	public AnalyzedObject(Object object, Class<?> effectiveType, Object effectiveObject) {
		this.object = object;
		this.effectiveType = effectiveType;
		this.effectiveObject = effectiveObject;
	}

}