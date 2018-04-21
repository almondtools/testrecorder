package net.amygdalum.testrecorder.serializers;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedField;

public interface SerializerFacade {

	SerializedValue serialize(Type type, Object object, SerializerSession session);

	SerializedValue[] serialize(Type[] clazzes, Object[] objects, SerializerSession session);

	SerializedField serialize(Field f, Object obj, SerializerSession session);

	SerializedValue serializePlaceholder(Type type, Object object, SerializerSession session);

	SerializerSession newSession();


}
