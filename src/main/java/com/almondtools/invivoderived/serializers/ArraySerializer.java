package com.almondtools.invivoderived.serializers;

import static java.util.Collections.emptyList;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

import com.almondtools.invivoderived.Serializer;
import com.almondtools.invivoderived.SerializerFacade;
import com.almondtools.invivoderived.values.SerializedArray;

public class ArraySerializer implements Serializer<SerializedArray> {

	private SerializerFacade facade;

	public ArraySerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return emptyList();
	}

	@Override
	public SerializedArray generate(Type type) {
		return new SerializedArray(type);
	}

	@Override
	public void populate(SerializedArray serializedObject, Object object) {
		for (int i = 0; i < Array.getLength(object); i++) {
			serializedObject.add(facade.serialize(serializedObject.getComponentType(), Array.get(object, i)));
		}
	}

}
