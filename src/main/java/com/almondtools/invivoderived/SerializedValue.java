package com.almondtools.invivoderived;

import java.lang.reflect.Type;

public interface SerializedValue {

	<T> T accept(SerializedValueVisitor<T> visitor);

	Type getType();

}
