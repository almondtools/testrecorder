package net.amygdalum.testrecorder.values;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.SerializedReferenceType;

public abstract class AbstractSerializedReferenceType extends AbstractSerializedValue implements SerializedReferenceType {

	private Type resultType;

	public AbstractSerializedReferenceType(Type type) {
		super(type);
	}

	@Override
	public Type getResultType() {
		if (resultType == null) {
			return getType();
		} else {
			return resultType;
		}
	}

	@Override
	public void setResultType(Type resultType) {
		this.resultType = resultType;
	}

}
