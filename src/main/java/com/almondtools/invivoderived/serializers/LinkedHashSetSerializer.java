package com.almondtools.invivoderived.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.almondtools.invivoderived.Serializer;
import com.almondtools.invivoderived.SerializerFacade;
import com.almondtools.invivoderived.SerializerFactory;
import com.almondtools.invivoderived.values.SerializedSet;

public class LinkedHashSetSerializer implements Serializer<SerializedSet>{

	private SerializerFacade facade;

	public LinkedHashSetSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(Set.class, HashSet.class, LinkedHashSet.class);
	}

	@Override
	public SerializedSet generate(Type type) {
		return new SerializedSet(type);
	}

	@Override
	public void populate(SerializedSet serializedObject, Object object) {
		for (Object element : (Set<?>) object) {
			serializedObject.add(facade.serialize(element.getClass(), element));
		}
	}

	public static class Factory implements SerializerFactory<SerializedSet> {

		@Override
		public LinkedHashSetSerializer newSerializer(SerializerFacade facade) {
			return new LinkedHashSetSerializer(facade);
		}

	}

}
