package com.almondtools.invivoderived.values;

import java.lang.reflect.Type;
import java.math.BigInteger;

import com.almondtools.invivoderived.SerializedImmutableVisitor;
import com.almondtools.invivoderived.SerializedValueVisitor;

public class SerializedBigInteger extends SerializedImmutable<BigInteger> {

	public SerializedBigInteger(Type type) {
		super(type);
	}

	@Override
	public <T> T accept(SerializedValueVisitor<T> visitor) {
		return visitor.as(SerializedImmutableVisitor.extend(visitor))
			.map(v -> v.visitBigInteger(this))
			.orElseGet(() -> visitor.visitUnknown(this));
	}

}
