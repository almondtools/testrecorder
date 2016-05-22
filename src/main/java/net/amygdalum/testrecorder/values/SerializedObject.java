package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.ValuePrinter;

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

	public SerializedObject withResult(Type resultType) {
		setResultType(resultType);
		return this;
	}

	public SerializedObject withFields(SerializedField... fields) {
		this.fields.addAll(asList(fields));
		return this;
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
	public List<SerializedValue> referencedValues() {
		return fields.stream()
			.map(field -> field.getValue())
			.collect(toList());
	}

	@Override
	public String toString() {
		return accept(new ValuePrinter());
	}

}
