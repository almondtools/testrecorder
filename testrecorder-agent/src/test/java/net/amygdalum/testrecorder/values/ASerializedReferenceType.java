package net.amygdalum.testrecorder.values;

import static java.util.Collections.emptyList;

import java.lang.reflect.Type;
import java.util.List;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.values.AbstractSerializedReferenceType;

public class ASerializedReferenceType extends AbstractSerializedReferenceType {
	public ASerializedReferenceType(Type type) {
		super(type);
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return emptyList();
	}

	@Override
	public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
		return null;
	}
}