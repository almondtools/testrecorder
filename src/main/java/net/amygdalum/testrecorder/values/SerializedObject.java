package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.deserializers.TypeManager.getBase;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;
import net.amygdalum.testrecorder.Deserializer;

public class SerializedObject implements SerializedReferenceType {

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

	public <T> T accept(Deserializer<T> visitor) {
		return visitor.visitReferenceType(this);
	}

	@Override
	public String toString() {
		return accept(new ValuePrinter());
	}

}
