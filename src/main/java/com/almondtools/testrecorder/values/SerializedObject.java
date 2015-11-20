package com.almondtools.testrecorder.values;

import static java.util.Arrays.asList;

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

	public SerializedObject withFields(SerializedField... fields) {
		this.fields.addAll(asList(fields));
		return this;
	}

	public SerializedObject withObjectType(Class<String> objectType) {
		this.objectType = objectType;
		return this;
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

}
