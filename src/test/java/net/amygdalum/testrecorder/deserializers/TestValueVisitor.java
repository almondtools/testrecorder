package net.amygdalum.testrecorder.deserializers;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedImmutableType;
import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValueType;
import net.amygdalum.testrecorder.values.SerializedField;

public class TestValueVisitor implements Deserializer<String> {

	@Override
	public String visitField(SerializedField fielddefault, DeserializerContext context) {
		return "field";
	}
	
	@Override
	public String visitReferenceType(SerializedReferenceType value, DeserializerContext context) {
		return value.getClass().getSimpleName();
	}
	
	@Override
	public String visitImmutableType(SerializedImmutableType value, DeserializerContext context) {
		return value.getClass().getSimpleName();
	}
	
	@Override
	public String visitValueType(SerializedValueType value, DeserializerContext context) {
		return value.getClass().getSimpleName();
	}
}