package com.almondtools.testrecorder.values;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.SerializedValueVisitor;
import com.almondtools.testrecorder.visitors.SerializedValuePrinter;

public class SerializedArray implements SerializedValue {

	private Type type;
	private List<SerializedValue> array;

	public SerializedArray(Type type) {
		this.type = type;
		this.array = new ArrayList<>();
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	public Type getComponentType() {
		if (type instanceof Class<?> && ((Class<?>) type).isArray()) {
			return ((Class<?>) type).getComponentType();
		} else if (type instanceof GenericArrayType) {
			return ((GenericArrayType) type).getGenericComponentType();
		} else {
			return Object.class;
		}
	}

	public SerializedValue[] getArray() {
		return array.toArray(new SerializedValue[0]);
	}

	@Override
	public <T> T accept(SerializedValueVisitor<T> visitor) {
		return visitor.visitArray(this);
	}
	
	public void add(SerializedValue value) {
		array.add(value);
	}

	@Override
	public String toString() {
		return accept(new SerializedValuePrinter());
	}

	@Override
	public int hashCode() {
		return type.getTypeName().hashCode() * 17
			+ array.hashCode();
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
		SerializedArray that = (SerializedArray) obj;
		return this.type == that.type
			&& this.array.equals(that.array);
	}
}
