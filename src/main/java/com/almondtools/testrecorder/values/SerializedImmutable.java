package com.almondtools.testrecorder.values;

import java.lang.reflect.Type;

import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.visitors.SerializedValuePrinter;

public abstract class SerializedImmutable<T> implements SerializedValue {

	private Type type;
	private T value;

	public SerializedImmutable(Type type) {
		this.type = type;
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

	public T getValue() {
		return value;
	}

	@Override
	public String toString() {
		return accept(new SerializedValuePrinter());
	}

}
