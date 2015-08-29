package com.almondtools.invivoderived;

public interface SerializerFactory<T extends SerializedValue> {

	Serializer<T> newSerializer(SerializerFacade facade);

}
