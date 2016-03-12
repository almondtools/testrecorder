package net.amygdalum.testrecorder;

import java.util.Optional;

import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;

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
