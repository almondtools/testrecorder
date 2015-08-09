package com.almondtools.iit.serializers;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.almondtools.iit.SerializedValue;
import com.almondtools.iit.Serializer;
import com.almondtools.iit.SerializerFacade;
import com.almondtools.iit.values.SerializedMap;

public class LinkedHashMapSerializer implements Serializer {

	private SerializerFacade facade;

	public LinkedHashMapSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(Map.class, HashMap.class, LinkedHashMap.class);
	}

	@Override
	public SerializedValue generate() {
		return new SerializedMap();
	}

	@Override
	public void populate(SerializedValue serializedObject, Object object) {
		for (Map.Entry<?,?> entry : ((Map<?,?>) object).entrySet()) {
			Object key = entry.getKey(); 
			Object value = entry.getValue();
			SerializedMap serializedMap = (SerializedMap) serializedObject;
			serializedMap.put(facade.serialize(key.getClass(), key), facade.serialize(value.getClass(), value));
		}
	}
}
