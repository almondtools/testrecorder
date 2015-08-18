package com.almondtools.invivoderived.values;

import java.util.ArrayList;
import java.util.List;

import com.almondtools.invivoderived.SerializedValue;
import com.almondtools.invivoderived.SerializedValueVisitor;
import com.almondtools.invivoderived.visitors.SerializedValuePrinter;

public class SerializedArray implements SerializedValue {

	private Class<?> type;
	private List<SerializedValue> array;

	public SerializedArray(Class<?> type) {
		this.type = type;
		this.array = new ArrayList<>();
	}
	
	@Override
	public Class<?> getType() {
		return type;
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
		return type.getName().hashCode() * 17
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
