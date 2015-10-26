package com.almondtools.testrecorder;

import java.lang.reflect.Type;

public class ValueSnapshot {

	private Class<?> declaringClass;
	private Type type;
	private String fieldName;

	private boolean valid;
	
	private SerializedValue value;

	public ValueSnapshot(Class<?> declaringClass, Type type, String fieldName) {
		this.declaringClass = declaringClass;
		this.type = type;
		this.fieldName = fieldName;
		this.valid = true;
	}

	public void invalidate() {
		valid = false;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public Type getValueType() {
		if (value == null) {
			return null;
		} else {
			return value.getType();
		}
	}
	
	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	public Type getType() {
		return type;
	}

	public String getFieldName() {
		return fieldName;
	}

	public SerializedValue getValue() {
		return value;
	}
	
	public void setValue(SerializedValue value) {
		this.value = value;
	}

}
