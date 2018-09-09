package net.amygdalum.testrecorder.types;

public class TestValueVisitor implements RoleVisitor<String> {

	@Override
	public String visitField(SerializedField fielddefault) {
		return "field";
	}
	
	@Override
	public String visitKeyValue(SerializedKeyValue keyvalue) {
		return "keyvalue";
	}
	
	@Override
	public String visitArgument(SerializedArgument argument) {
		return "argument" + argument.getIndex();
	}
	
	@Override
	public String visitResult(SerializedResult result) {
		return "result";
	}
	
	@Override
	public String visitReferenceType(SerializedReferenceType value) {
		return "ReferenceType:" + value.getClass().getSimpleName();
	}
	
	@Override
	public String visitImmutableType(SerializedImmutableType value) {
		return "ImmutableType:" + value.getClass().getSimpleName();
	}
	
	@Override
	public String visitValueType(SerializedValueType value) {
		return "ValueType:" + value.getClass().getSimpleName();
	}
}