package com.almondtools.invivoderived;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import com.almondtools.invivoderived.values.SerializedField;

public interface SerializerFacade {

	SerializedValue serialize(Type type, Object object);

	SerializedValue[] serialize(Type[] clazzes, Object[] objects);

	SerializedField serialize(Field f, Object obj);

	boolean excludes(Class<?> type);

}