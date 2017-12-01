package net.amygdalum.testrecorder.serializers;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;

public interface SerializerFactory<T extends SerializedValue> {

	Serializer<T> newSerializer(SerializerFacade facade);

}
