package net.amygdalum.testrecorder.types;

public interface Deserializer<T> {

    T visitField(SerializedFieldType field, DeserializerContext context);

	T visitReferenceType(SerializedReferenceType value, DeserializerContext context);

	T visitImmutableType(SerializedImmutableType value, DeserializerContext context);

	T visitValueType(SerializedValueType value, DeserializerContext context);

}
