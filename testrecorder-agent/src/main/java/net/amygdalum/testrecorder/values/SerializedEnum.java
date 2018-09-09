package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;

import java.util.List;

import net.amygdalum.testrecorder.types.RoleVisitor;
import net.amygdalum.testrecorder.types.ReferenceTypeVisitor;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedValue;

/**
 * Serializing to SerializedEnum is restricted to enums. It is recommended not to use another enum implementation. 
 */
public class SerializedEnum extends AbstractSerializedReferenceType implements SerializedImmutableType {

	private String name;

	public SerializedEnum(Class<?> type) {
		super(type);
	}

	public SerializedEnum withName(String name) {
		this.name = name;
		return this;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return emptyList();
	}

	public <T> T accept(RoleVisitor<T> visitor) {
		return visitor.visitImmutableType(this);
	}

	@Override
	public <T> T accept(ReferenceTypeVisitor<T> visitor) {
		return visitor.visitImmutableType(this);
	}

	@Override
	public String toString() {
		return ValuePrinter.print(this);
	}

}
