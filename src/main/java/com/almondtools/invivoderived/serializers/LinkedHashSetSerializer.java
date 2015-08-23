package com.almondtools.invivoderived.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.almondtools.invivoderived.SerializedValue;
import com.almondtools.invivoderived.Serializer;
import com.almondtools.invivoderived.SerializerFacade;
import com.almondtools.invivoderived.values.SerializedSet;

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
	public SerializedValue generate(Type type) {
		return new SerializedSet(type);
	}

	@Override
	public void populate(SerializedValue serializedObject, Object object) {
		for (Object element : (Set<?>) object) {
			((SerializedSet) serializedObject).add(facade.serialize(element.getClass(), element));
		}
	}

}
