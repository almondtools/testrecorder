package com.almondtools.testrecorder;

import java.util.Optional;

import com.almondtools.testrecorder.values.SerializedArray;
import com.almondtools.testrecorder.values.SerializedField;
import com.almondtools.testrecorder.values.SerializedLiteral;
import com.almondtools.testrecorder.values.SerializedNull;
import com.almondtools.testrecorder.values.SerializedObject;

public interface SerializedValueVisitor<T> {

	T visitField(SerializedField field);

	T visitObject(SerializedObject value);

	T visitArray(SerializedArray value);

	T visitLiteral(SerializedLiteral value);

	T visitNull(SerializedNull value);

	T visitUnknown(SerializedValue value);

	default <S extends SerializedValueVisitor<T>> Optional<S> as(Class<S> descriptor) {
		if (descriptor.isInstance(this)) {
			return Optional.of(descriptor.cast(this));
		} else {
			return Optional.empty();
		}
	}

}
