package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultQueueSerializer implements Serializer<SerializedList> {

	private SerializerFacade facade;

	public DefaultQueueSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(LinkedBlockingQueue.class, ArrayBlockingQueue.class, ConcurrentLinkedQueue.class, PriorityBlockingQueue.class, LinkedTransferQueue.class, DelayQueue.class);
	}

	@Override
	public SerializedList generate(Type type) {
		return new SerializedList(type);
	}

	@Override
	public void populate(SerializedList serializedObject, Object object) {
		Type resultType = serializedObject.getComponentType();
		for (Object element : (Queue<?>) object) {
			serializedObject.add(facade.serialize(resultType, element));
		}
	}

}
