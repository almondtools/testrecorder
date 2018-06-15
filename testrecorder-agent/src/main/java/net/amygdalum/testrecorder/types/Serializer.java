package net.amygdalum.testrecorder.types;

import java.util.List;

public interface Serializer<T extends SerializedValue> {

	List<Class<?>> getMatchingClasses();

	T generate(Class<?> type, SerializerSession session);

	void populate(T serializedObject, Object object, SerializerSession session);
}
