package com.almondtools.invivoderived.values;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import com.almondtools.invivoderived.SerializedImmutableVisitor;
import com.almondtools.invivoderived.SerializedValueVisitor;

public class SerializedBigDecimal extends SerializedImmutable<BigDecimal> {

	public SerializedBigDecimal(Type type) {
		super(type);
	}

	@Override
	public <T> T accept(SerializedValueVisitor<T> visitor) {
		return visitor.as(SerializedImmutableVisitor.extend(visitor))
			.map(v -> v.visitBigDecimal(this))
			.orElseGet(() -> visitor.visitUnknown(this));
	}

}
