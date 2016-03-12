package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.visitors.TypeManager.getBase;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.SerializedValueVisitor;
import net.amygdalum.testrecorder.visitors.SerializedValuePrinter;

public class SerializedObject implements SerializedValue {

	private Type type;
	private Class<?> valueType;
	private List<SerializedField> fields;

	public SerializedObject(Type type, Class<?> valueType) {
		this.type = type;
		this.valueType = valueType;
		this.fields = new ArrayList<>();
	}

	public SerializedObject withFields(SerializedField... fields) {
		this.fields.addAll(asList(fields));
		return this;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Class<?> getValueType() {
		return getBase(valueType);
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
