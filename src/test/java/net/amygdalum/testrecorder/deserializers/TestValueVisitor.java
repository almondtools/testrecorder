package net.amygdalum.testrecorder.deserializers;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;
import net.amygdalum.testrecorder.types.SerializedFieldType;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValueType;

public class TestValueVisitor implements Deserializer<String> {

	@Override
	public String visitField(SerializedFieldType fielddefault, DeserializerContext context) {
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