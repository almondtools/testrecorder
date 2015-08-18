package com.almondtools.invitroderivatives.serializers;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import com.almondtools.invitroderivatives.SerializedValue;
import com.almondtools.invitroderivatives.Serializer;
import com.almondtools.invitroderivatives.SerializerFacade;
import com.almondtools.invitroderivatives.values.SerializedList;

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
