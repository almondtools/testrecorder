package net.amygdalum.testrecorder.values;

import java.lang.reflect.Type;
import java.math.BigDecimal;

import net.amygdalum.testrecorder.SerializedImmutableVisitor;
import net.amygdalum.testrecorder.SerializedValueVisitor;

public class SerializedBigDecimal extends SerializedImmutable<BigDecimal> {

	public SerializedBigDecimal(Type type, Class<?> valueType) {
		super(type, valueType);
	}
	
	@Override
	public Class<?> getValueType() {
		return BigDecimal.class;
	}

	@Override
	public <T> T accept(SerializedValueVisitor<T> visitor) {
		return visitor.as(SerializedImmutableVisitor.extend(visitor))
			.map(v -> v.visitBigDecimal(this))
			.orElseGet(() -> visitor.visitUnknown(this));
	}

}
