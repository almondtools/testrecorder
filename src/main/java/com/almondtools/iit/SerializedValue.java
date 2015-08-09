package com.almondtools.iit;

public interface SerializedValue {

	<T> T accept(SerializedValueVisitor<T> visitor);

	Class<?> getType();

}
