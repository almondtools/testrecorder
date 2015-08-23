package com.almondtools.invivoderived;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.almondtools.invivoderived.values.SerializedField;

public interface SerializerFacade {

	Set<Class<?>> LITERAL_TYPES = new HashSet<>(Arrays.asList(
		boolean.class, char.class, byte.class, short.class, int.class, float.class, long.class, double.class,
		Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Float.class, Long.class, Double.class,
		String.class));
	
	SerializedValue serialize(Type type, Object object);

	SerializedValue[] serialize(Type[] clazzes, Object[] objects);

	SerializedField serialize(Field f, Object obj);

	boolean excludes(Class<?> type);

}