package com.almondtools.testrecorder.values;

import java.lang.reflect.Type;
import java.math.BigInteger;

import com.almondtools.testrecorder.SerializedImmutableVisitor;
import com.almondtools.testrecorder.SerializedValueVisitor;

public class SerializedBigInteger extends SerializedImmutable<BigInteger> {

	public SerializedBigInteger(Type type, Class<?> valueType) {
		super(type, valueType);
	}

	@Override
	public <T> T accept(SerializedValueVisitor<T> visitor) {
		return visitor.as(SerializedImmutableVisitor.extend(visitor))
			.map(v -> v.visitBigInteger(this))
			.orElseGet(() -> visitor.visitUnknown(this));
	}

}
