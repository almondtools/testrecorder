package com.almondtools.iit.values;

import java.util.ArrayList;
import java.util.List;

import com.almondtools.iit.SerializedValue;
import com.almondtools.iit.SerializedValueVisitor;
import com.almondtools.iit.visitors.SerializedValuePrinter;

public class SerializedObject implements SerializedValue {

	private Class<?> type;
	private List<SerializedField> fields;

	public SerializedObject(Class<?> type) {
		this.type = type;
		this.fields = new ArrayList<>();
	}
	
	@Override
	public Class<?> getType() {
		return type;
	}
	
	public List<SerializedField> getFields() {
		return fields;
	}

	public void addField(SerializedField field) {
		fields.add(field);
	}
	
	public <T> T accept(SerializedValueVisitor<T> visitor) {
		return visitor.visitObject(this);
	}

	@Override
	public String toString() {
		return accept(new SerializedValuePrinter());
	}

	@Override
	public int hashCode() {
		return type.getName().hashCode() + fields.hashCode();
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
		SerializedObject that = (SerializedObject) obj;
		return this.fields.equals(that.fields)
			&& this.type == that.type;
	}
	
}
