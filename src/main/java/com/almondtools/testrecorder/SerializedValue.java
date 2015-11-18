package com.almondtools.testrecorder;

import java.lang.reflect.Type;

public interface SerializedValue {

	<T> T accept(SerializedValueVisitor<T> visitor);

	Type getType();

	int shortHashcode();

}
