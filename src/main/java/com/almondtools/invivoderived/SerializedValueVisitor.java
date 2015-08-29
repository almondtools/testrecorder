package com.almondtools.invivoderived;

import java.util.Optional;

import com.almondtools.invivoderived.values.SerializedArray;
import com.almondtools.invivoderived.values.SerializedField;
import com.almondtools.invivoderived.values.SerializedLiteral;
import com.almondtools.invivoderived.values.SerializedNull;
import com.almondtools.invivoderived.values.SerializedObject;

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
