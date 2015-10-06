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

	@Override
	public int hashCode() {
		return type.getTypeName().hashCode() * 19
			+ value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SerializedImmutable<?> that = (SerializedImmutable<?>) obj;
		return this.type == that.type
			&& this.value.equals(that.value);
	}

}
