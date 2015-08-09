package com.almondtools.iit.values;

import com.almondtools.iit.SerializedValue;
import com.almondtools.iit.SerializedValueVisitor;
import com.almondtools.iit.visitors.SerializedValuePrinter;

public class SerializedField {

	private String name;
	private Class<?> type;
	private SerializedValue value;

	public SerializedField(String name, Class<?> type, SerializedValue value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public Class<?> getType() {
		return type;
	}

	public SerializedValue getValue() {
		return value;
	}

	public <T> T accept(SerializedValueVisitor<T> visitor) {
		return visitor.visitField(this);
	}

	@Override
	public String toString() {
		return accept(new SerializedValuePrinter());
	}

	@Override
	public int hashCode() {
		return name.hashCode() * 31
			+ type.getName().hashCode() * 13
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
		SerializedField that = (SerializedField) obj;
		return this.name.equals(that.name)
			&& this.type == that.type
			&& this.value.equals(that.value);
	}
}
