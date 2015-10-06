package com.almondtools.testrecorder;

import com.almondtools.testrecorder.values.SerializedBigDecimal;
import com.almondtools.testrecorder.values.SerializedBigInteger;

public interface SerializedImmutableVisitor<T> extends SerializedValueVisitor<T> {

	T visitBigDecimal(SerializedBigDecimal value);

	T visitBigInteger(SerializedBigInteger value);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static <S> Class<SerializedImmutableVisitor<S>> extend(SerializedValueVisitor<S> visitor) {
		return (Class) SerializedImmutableVisitor.class;
	}
	
}
