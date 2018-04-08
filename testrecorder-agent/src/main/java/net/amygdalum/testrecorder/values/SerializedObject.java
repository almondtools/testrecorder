package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;

/**
 * Serializing to SerializedObject is the default if no other SerializedValue matches.
 * 
 * There is no restriction to objects that are serialized in this way other than being non-primitive.
 */
public class SerializedObject extends AbstractSerializedReferenceType implements SerializedReferenceType {

	private List<SerializedField> fields;

	public SerializedObject(Type type) {
		super(type);
		this.fields = new ArrayList<>();
	}

	public SerializedObject withFields(List<SerializedField> fields) {
		this.fields.addAll(fields);
		return this;
	}

	public SerializedObject withFields(SerializedField... fields) {
		return withFields(asList(fields));
	}

	public List<SerializedField> getFields() {
		return fields;
	}

	public Optional<SerializedField> getField(String name) {
		return fields.stream()
			.filter(field -> name.equals(field.getName()))
			.findFirst();
	}

	public void addField(SerializedField field) {
		fields.add(field);
	}

	public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
		return visitor.visitReferenceType(this, context);
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return fields.stream()
			.map(field -> field.getValue())
			.collect(toList());
	}

	@Override
	public String toString() {
		return ValuePrinter.print(this);
	}

}
