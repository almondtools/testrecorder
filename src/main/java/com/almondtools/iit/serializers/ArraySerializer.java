package com.almondtools.iit.serializers;

import static java.util.Collections.emptyList;

import java.lang.reflect.Array;
import java.util.List;

import com.almondtools.iit.SerializedValue;
import com.almondtools.iit.Serializer;
import com.almondtools.iit.SerializerFacade;
import com.almondtools.iit.values.SerializedArray;

public class ArraySerializer implements Serializer {

	private SerializerFacade facade;
	private Class<?> type;

	public ArraySerializer(SerializerFacade facade, Class<?> type) {
		this.facade = facade;
		this.type = type;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return emptyList();
	}

	@Override
	public SerializedValue generate() {
		return new SerializedArray(type);
	}

	@Override
	public void populate(SerializedValue serializedObject, Object object) {
		SerializedArray newSerializedObject = (SerializedArray) serializedObject;
		for (int i = 0; i < Array.getLength(object); i++) {
			newSerializedObject.add(facade.serialize(type, Array.get(object, i)));
		}
	}

}
