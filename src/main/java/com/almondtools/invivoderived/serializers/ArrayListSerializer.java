package com.almondtools.invivoderived.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.almondtools.invivoderived.Serializer;
import com.almondtools.invivoderived.SerializerFacade;
import com.almondtools.invivoderived.SerializerFactory;
import com.almondtools.invivoderived.values.SerializedList;

public class ArrayListSerializer implements Serializer<SerializedList> {

	private SerializerFacade facade;

	public ArrayListSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(List.class, ArrayList.class);
	}

	@Override
	public SerializedList generate(Type type) {
		return new SerializedList(type);
	}

	@Override
	public void populate(SerializedList serializedObject, Object object) {
		for (Object element : (List<?>) object) {
			serializedObject.add(facade.serialize(element.getClass(), element));
		}
	}

	public static class Factory implements SerializerFactory<SerializedList> {

		@Override
		public ArrayListSerializer newSerializer(SerializerFacade facade) {
			return new ArrayListSerializer(facade);
		}

	}

}
