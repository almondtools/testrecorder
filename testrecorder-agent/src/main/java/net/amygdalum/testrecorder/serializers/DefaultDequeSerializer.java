package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultDequeSerializer extends AbstractCompositeSerializer implements Serializer<SerializedList> {

	public DefaultDequeSerializer() {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(ArrayDeque.class, ConcurrentLinkedDeque.class, LinkedBlockingDeque.class);
	}

	@Override
	public Stream<?> components(Object object, SerializerSession session) {
		return ((Deque<?>) object).stream();
	}

	@Override
	public SerializedList generate(Class<?> type, SerializerSession session) {
		return new SerializedList(type);
	}

	@Override
	public void populate(SerializedList serializedObject, Object object, SerializerSession session) {
		for (Object element : (Deque<?>) object) {
			serializedObject.add(resolvedValueOf(session, serializedObject.getComponentType(), element));
		}
	}

}
