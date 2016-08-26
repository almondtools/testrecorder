package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;

import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.SerializerFactory;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultDequeSerializer implements Serializer<SerializedList> {

	private SerializerFacade facade;

	public DefaultDequeSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(ArrayDeque.class, ConcurrentLinkedDeque.class, LinkedBlockingDeque.class);
	}

	@Override
	public SerializedList generate(Type resultType, Type type) {
		return new SerializedList(type).withResult(resultType);
	}

	@Override
	public void populate(SerializedList serializedObject, Object object) {
		Type resultType = serializedObject.getComponentType();
		for (Object element : (Deque<?>) object) {
			serializedObject.add(facade.serialize(resultType, element));
		}
	}

	public static class Factory implements SerializerFactory<SerializedList> {

		@Override
		public DefaultDequeSerializer newSerializer(SerializerFacade facade) {
			return new DefaultDequeSerializer(facade);
		}

	}

}
