package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class ClassSerializer implements Serializer<SerializedImmutable<Class<?>>> {

	public ClassSerializer() {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(Class.class);
	}

	@Override
	public Stream<?> components(Object object, SerializerSession session) {
		return Stream.empty();
	}

	@Override
	public SerializedImmutable<Class<?>> generate(Class<?> type, SerializerSession session) {
		return new SerializedImmutable<>(type);
	}

	@Override
	public void populate(SerializedImmutable<Class<?>> serializedObject, Object object, SerializerSession session) {
		serializedObject.setValue((Class<?>) object);
	}

}
