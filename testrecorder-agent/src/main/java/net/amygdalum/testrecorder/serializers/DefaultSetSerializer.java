package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetSerializer extends AbstractCompositeSerializer implements Serializer<SerializedSet> {

	public DefaultSetSerializer() {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(HashSet.class, LinkedHashSet.class, TreeSet.class);
	}

	@Override
	public Stream<?> components(Object object, SerializerSession session) {
		return ((Set<?>) object).stream();
	}

	@Override
	public SerializedSet generate(Class<?> type, SerializerSession session) {
		return new SerializedSet(type);
	}

	@Override
	public void populate(SerializedSet serializedObject, Object object, SerializerSession session) {
		for (Object element : (Set<?>) object) {
			serializedObject.add(resolvedValueOf(session, serializedObject.getComponentType(), element));
		}
	}

}
