package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.values.SerializedField;

public interface Deserializer<T> {

	T visitField(SerializedField field);

	T visitReferenceType(SerializedReferenceType value);

	T visitImmutableType(SerializedImmutableType value);

	T visitValueType(SerializedValueType value);

}
