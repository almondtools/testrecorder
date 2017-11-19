package net.amygdalum.testrecorder.values;

import java.lang.reflect.Type;
import java.util.List;

import net.amygdalum.testrecorder.Deserializer;
import net.amygdalum.testrecorder.SerializedImmutableType;
import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.DeserializerContext;
import net.amygdalum.testrecorder.runtime.LambdaSignature;

public class SerializedLambdaObject extends AbstractSerializedReferenceType implements SerializedImmutableType {

	private LambdaSignature signature;
	private List<SerializedValue> capturedArguments;

	public SerializedLambdaObject(Type type) {
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
	public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
		return visitor.visitReferenceType(this, context);
	}

	@Override
	public List<SerializedValue> referencedValues() {
		return capturedArguments;
	}

}
