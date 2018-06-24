package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultQueueSerializer extends AbstractCompositeSerializer implements Serializer<SerializedList> {

	public DefaultQueueSerializer() {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(LinkedBlockingQueue.class, ArrayBlockingQueue.class, ConcurrentLinkedQueue.class, PriorityBlockingQueue.class, LinkedTransferQueue.class, DelayQueue.class);
	}

	@Override
	public Stream<?> components(Object object, SerializerSession session) {
		return ((Queue<?>) object).stream();
	}

	@Override
	public SerializedList generate(Class<?> type, SerializerSession session) {
		return new SerializedList(type);
	}

	@Override
	public void populate(SerializedList serializedObject, Object object, SerializerSession session) {
		for (Object element : (Queue<?>) object) {
			serializedObject.add(resolvedValueOf(session, serializedObject.getComponentType(), element));
		}
	}

}
