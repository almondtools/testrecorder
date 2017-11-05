package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.deserializers.DeserializerContext;
import net.amygdalum.testrecorder.values.SerializedField;

public interface Deserializer<T> {

    T visitField(SerializedField field, DeserializerContext context);

	T visitReferenceType(SerializedReferenceType value, DeserializerContext context);

	T visitImmutableType(SerializedImmutableType value, DeserializerContext context);

	T visitValueType(SerializedValueType value, DeserializerContext context);

}
