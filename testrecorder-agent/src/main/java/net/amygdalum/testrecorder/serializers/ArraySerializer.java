package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.util.Types.isPrimitive;

import java.lang.reflect.Array;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedArray;

public class ArraySerializer extends AbstractCompositeSerializer implements Serializer<SerializedArray> {

	public ArraySerializer() {
	}

	@Override
	public Stream<?> components(Object object, SerializerSession session) {
		Class<?> type = object.getClass().getComponentType();
		if (isPrimitive(type)) {
			return Stream.empty();
		}
		Builder<Object> components = Stream.builder();
		for (int i = 0; i < Array.getLength(object); i++) {
			Object component = Array.get(object, i);
			components.add(component);
		}
		return components.build();
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return emptyList();
	}

	@Override
	public SerializedArray generate(Class<?> type, SerializerSession session) {
		return new SerializedArray(type);
	}

	@Override
	public void populate(SerializedArray serializedObject, Object object, SerializerSession session) {
		Class<?> type = object.getClass().getComponentType();
		for (int i = 0; i < Array.getLength(object); i++) {
			Object component = Array.get(object, i);
			serializedObject.add(resolvedValueOf(session, type, component));
		}
	}

}
