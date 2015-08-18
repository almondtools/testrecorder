package com.almondtools.invitroderivatives;

public interface SerializedValue {

	<T> T accept(SerializedValueVisitor<T> visitor);

	Class<?> getType();

}
