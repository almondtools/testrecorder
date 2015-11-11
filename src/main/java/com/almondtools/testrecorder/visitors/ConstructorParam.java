package com.almondtools.testrecorder.visitors;

import java.lang.reflect.Constructor;

import com.almondtools.testrecorder.values.SerializedField;

public class ConstructorParam {

	private Constructor<?> constructor;
	private int paramNumber;
	private SerializedField field;
	private Object value;

	public ConstructorParam(Constructor<?> constructor, int paramNumber, SerializedField field, Object value) {
		this.constructor = constructor;
		this.paramNumber = paramNumber;
		this.field = field;
		this.value = value;
	}
	
	public Constructor<?> getConstructor() {
		return constructor;
	}
	
	public int getParamNumber() {
		return paramNumber;
	}
	
	public SerializedField getField() {
		return field;
	}
	
	public Object getValue() {
		return value;
	}

}
