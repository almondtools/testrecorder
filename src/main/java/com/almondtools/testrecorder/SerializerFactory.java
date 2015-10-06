package com.almondtools.testrecorder;

public interface SerializerFactory<T extends SerializedValue> {

	Serializer<T> newSerializer(SerializerFacade facade);

}
