package com.almondtools.invivoderived;

public interface SerializedValue {

	<T> T accept(SerializedValueVisitor<T> visitor);

	Class<?> getType();

}
