package net.amygdalum.testrecorder.values;

import java.util.List;

import net.amygdalum.testrecorder.types.RoleVisitor;
import net.amygdalum.testrecorder.types.ReferenceTypeVisitor;
import net.amygdalum.testrecorder.types.SerializedImmutableType;
import net.amygdalum.testrecorder.types.SerializedValue;

public class SerializedLambdaObject extends AbstractSerializedReferenceType implements SerializedImmutableType {

	private LambdaSignature signature;
	private List<SerializedValue> capturedArguments;

	public SerializedLambdaObject(Class<?> type) {
		super(type);
	}

	public void setSignature(LambdaSignature signature) {
		this.signature = signature;
	}

	public LambdaSignature getSignature() {
		return signature;
	}

	public List<SerializedValue> getCapturedArguments() {
		return capturedArguments;
	}

	public void setCapturedArguments(List<SerializedValue> capturedArguments) {
		this.capturedArguments = capturedArguments;
	}

	@Override
	public <T> T accept(RoleVisitor<T> visitor) {
		return visitor.visitReferenceType(this);
	}

	@Override
	public <T> T accept(ReferenceTypeVisitor<T> visitor) {
		return visitor.visitImmutableType(this);
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return capturedArguments;
	}

}
