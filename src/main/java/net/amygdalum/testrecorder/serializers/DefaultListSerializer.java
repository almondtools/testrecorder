package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.SerializerFactory;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultListSerializer implements Serializer<SerializedList> {

	private SerializerFacade facade;

	public DefaultListSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(LinkedList.class, ArrayList.class);
	}

	@Override
	public SerializedList generate(Type resultType, Type type) {
		return new SerializedList(type).withResult(resultType);
	}

	@Override
	public void populate(SerializedList serializedObject, Object object) {
		Type resultType = serializedObject.getComponentType();
		for (Object element : (List<?>) object) {
			serializedObject.add(facade.serialize(resultType, element));
		}
	}

	public static class Factory implements SerializerFactory<SerializedList> {

		@Override
		public DefaultListSerializer newSerializer(SerializerFacade facade) {
			return new DefaultListSerializer(facade);
		}

	}

}
