package net.amygdalum.testrecorder;

import java.lang.reflect.Type;
import java.util.List;

public interface Serializer<T extends SerializedValue> {

	List<Class<?>> getMatchingClasses();

	T generate(Type type, Class<?> valueType);

	void populate(T serializedObject, Object object);
}
