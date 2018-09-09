package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;

import java.util.List;

import net.amygdalum.testrecorder.types.ReferenceTypeVisitor;
import net.amygdalum.testrecorder.types.RoleVisitor;
import net.amygdalum.testrecorder.types.SerializedValue;

public class ASerializedReferenceType extends AbstractSerializedReferenceType {

	public ASerializedReferenceType(Class<?> type) {
		super(type);
	}

	public ASerializedReferenceType(Class<?> type, int id) {
		super(type);
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return emptyList();
	}

	@Override
	public <T> T accept(RoleVisitor<T> visitor) {
		return null;
	}

	@Override
	public <T> T accept(ReferenceTypeVisitor<T> visitor) {
		return null;
	}
}