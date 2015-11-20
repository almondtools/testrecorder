package com.almondtools.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.almondtools.testrecorder.Serializer;
import com.almondtools.testrecorder.SerializerFacade;
import com.almondtools.testrecorder.SerializerFactory;
import com.almondtools.testrecorder.values.SerializedSet;

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
	public SerializedSet generate(Type type, Class<?> valueType) {
		return new SerializedSet(type, valueType);
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
