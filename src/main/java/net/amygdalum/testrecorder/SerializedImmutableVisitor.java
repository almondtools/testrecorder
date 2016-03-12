package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.values.SerializedBigDecimal;
import net.amygdalum.testrecorder.values.SerializedBigInteger;

public interface SerializedImmutableVisitor<T> extends SerializedValueVisitor<T> {

	T visitBigDecimal(SerializedBigDecimal value);

	T visitBigInteger(SerializedBigInteger value);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static <S> Class<SerializedImmutableVisitor<S>> extend(SerializedValueVisitor<S> visitor) {
		return (Class) SerializedImmutableVisitor.class;
	}
	
}
