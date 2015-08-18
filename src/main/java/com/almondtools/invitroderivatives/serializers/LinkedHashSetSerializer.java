package com.almondtools.invitroderivatives.serializers;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.almondtools.invitroderivatives.SerializedValue;
import com.almondtools.invitroderivatives.Serializer;
import com.almondtools.invitroderivatives.SerializerFacade;
import com.almondtools.invitroderivatives.values.SerializedSet;

public class LinkedHashSetSerializer implements Serializer {

	private SerializerFacade facade;

	public LinkedHashSetSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(Set.class, HashSet.class, LinkedHashSet.class);
	}

	@Override
	public SerializedValue generate() {
		return new SerializedSet();
	}

	@Override
	public void populate(SerializedValue serializedObject, Object object) {
		for (Object element : (Set<?>) object) {
			((SerializedSet) serializedObject).add(facade.serialize(element.getClass(), element));
		}
	}
}
