package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.amygdalum.testrecorder.types.RoleVisitor;
import net.amygdalum.testrecorder.types.ReferenceTypeVisitor;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedStructuralType;
import net.amygdalum.testrecorder.types.SerializedValue;

/**
 * Serializing to SerializedObject is the default if no other SerializedValue matches.
 * 
 * There is no restriction to objects that are serialized in this way other than being non-primitive.
 */
public class SerializedObject extends AbstractSerializedReferenceType implements SerializedStructuralType {

	private List<SerializedField> fields;

	public SerializedObject(Class<?> type) {
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

	@Override
	public List<SerializedField> fields() {
		return new ArrayList<>(fields);
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

	public <T> T accept(RoleVisitor<T> visitor) {
		return visitor.visitReferenceType(this);
	}

	@Override
	public <T> T accept(ReferenceTypeVisitor<T> visitor) {
		return visitor.visitStructuralType(this);
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
