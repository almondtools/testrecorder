package com.almondtools.testrecorder.values;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.almondtools.testrecorder.SerializedValue;
import com.almondtools.testrecorder.SerializedValueVisitor;
import com.almondtools.testrecorder.visitors.SerializedValuePrinter;

public class SerializedObject implements SerializedValue {

	private Type type;
	private Class<?> objectType;
	private List<SerializedField> fields;

	public SerializedObject(Type type) {
		this.type = type;
		this.fields = new ArrayList<>();
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	public void setObjectType(Class<?> objectType) {
		this.objectType = objectType;
	}
	
	public Class<?> getObjectType() {
		return objectType;
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
		return objectType.getName().hashCode() + fields.hashCode();
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
			&& this.objectType == that.objectType;
	}
	
}
