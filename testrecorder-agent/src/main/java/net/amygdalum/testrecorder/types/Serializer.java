package net.amygdalum.testrecorder.types;

import java.lang.reflect.Type;
import java.util.List;

public interface Serializer<T extends SerializedValue> {

	List<Class<?>> getMatchingClasses();

	T generate(Type type, SerializerSession session);

	void populate(T serializedObject, Object object, SerializerSession session);
}
