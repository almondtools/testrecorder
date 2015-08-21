package com.almondtools.invivoderived.serializers;

import static java.util.Collections.emptyList;

import java.lang.reflect.Array;
import java.util.List;

import com.almondtools.invivoderived.SerializedValue;
import com.almondtools.invivoderived.Serializer;
import com.almondtools.invivoderived.SerializerFacade;
import com.almondtools.invivoderived.values.SerializedArray;

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
			newSerializedObject.add(facade.serialize(type.getComponentType(), Array.get(object, i)));
		}
	}

}
