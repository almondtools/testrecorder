package com.almondtools.invivoderived;

import java.lang.reflect.Type;
import java.util.List;

public interface Serializer {

	List<Class<?>> getMatchingClasses();

	SerializedValue generate(Type type);

	void populate(SerializedValue serializedObject, Object object);
}
