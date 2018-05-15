package net.amygdalum.testrecorder.types;

public class TestValueVisitor implements Deserializer<String> {

	@Override
	public String visitField(SerializedFieldType fielddefault, DeserializerContext context) {
		return "field";
	}
	
	@Override
	public String visitReferenceType(SerializedReferenceType value, DeserializerContext context) {
		return "ReferenceType:" + value.getClass().getSimpleName();
	}
	
	@Override
	public String visitImmutableType(SerializedImmutableType value, DeserializerContext context) {
		return "ImmutableType:" + value.getClass().getSimpleName();
	}
	
	@Override
	public String visitValueType(SerializedValueType value, DeserializerContext context) {
		return "ValueType:" + value.getClass().getSimpleName();
	}
}