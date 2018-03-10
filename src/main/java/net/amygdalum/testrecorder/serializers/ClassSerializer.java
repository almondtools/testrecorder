package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.List;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class ClassSerializer implements Serializer<SerializedImmutable<Class<?>>> {

	public ClassSerializer(SerializerFacade facade) {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(Class.class);
	}

	@Override
	public SerializedImmutable<Class<?>> generate(Type type) {
		return new SerializedImmutable<>(type);
	}

	@Override
	public void populate(SerializedImmutable<Class<?>> serializedObject, Object object) {
		serializedObject.setValue((Class<?>) object);
	}

}
