package com.almondtools.iit.serializers;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import com.almondtools.iit.SerializedValue;
import com.almondtools.iit.Serializer;
import com.almondtools.iit.SerializerFacade;
import com.almondtools.iit.values.SerializedList;

public class ArrayListSerializer implements Serializer {

	private SerializerFacade facade;

	public ArrayListSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(List.class, ArrayList.class);
	}

	@Override
	public SerializedValue generate() {
		return new SerializedList();
	}

	@Override
	public void populate(SerializedValue serializedObject, Object object) {
		for (Object element : (List<?>) object) {
			((SerializedList) serializedObject).add(facade.serialize(element.getClass(), element));
		}
	}
}
