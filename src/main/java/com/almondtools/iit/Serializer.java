package com.almondtools.iit;

import java.util.List;

public interface Serializer {

	List<Class<?>> getMatchingClasses();

	SerializedValue generate();

	void populate(SerializedValue serializedObject, Object object);
}
