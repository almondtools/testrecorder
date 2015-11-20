package com.almondtools.testrecorder.values;

import java.lang.reflect.Type;

import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.visitors.SerializedValuePrinter;

public abstract class SerializedImmutable<T> implements SerializedValue {

	private Type type;
	private Class<?> valueType;
	private T value;

	public SerializedImmutable(Type type, Class<?> valueType) {
		this.type = type;
		this.valueType = valueType;
	}

	public SerializedImmutable<T> withValue(T value) {
		this.value = value;
		return this;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public Class<?> getValueType() {
		return valueType;
	}

	public T getValue() {
		return value;
	}

	@Override
	public String toString() {
		return accept(new SerializedValuePrinter());
	}

}
