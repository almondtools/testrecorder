package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultListSerializer extends AbstractCompositeSerializer implements Serializer<SerializedList> {

	public DefaultListSerializer() {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(LinkedList.class, ArrayList.class, Vector.class);
	}

	@Override
	public Stream<?> components(Object object, SerializerSession session) {
		return ((List<?>) object).stream();
	}

	@Override
	public SerializedList generate(Class<?> type, SerializerSession session) {
		return new SerializedList(type);
	}

	@Override
	public void populate(SerializedList serializedObject, Object object, SerializerSession session) {
		for (Object element : (List<?>) object) {
			serializedObject.add(resolvedValueOf(session, serializedObject.getComponentType(), element));
		}
	}

}
