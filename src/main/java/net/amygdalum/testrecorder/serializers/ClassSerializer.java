package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.SerializerFactory;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class ClassSerializer implements Serializer<SerializedImmutable<Class<?>>> {

	public ClassSerializer(SerializerFacade facade) {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(BigDecimal.class);
	}

	@Override
	public SerializedImmutable<Class<?>> generate(Type resultType, Type type) {
		return new SerializedImmutable<>(type);
	}

	@Override
	public void populate(SerializedImmutable<Class<?>> serializedObject, Object object) {
		serializedObject.setValue((Class<?>) object);
	}

	public static class Factory implements SerializerFactory<SerializedImmutable<Class<?>>> {

		@Override
		public ClassSerializer newSerializer(SerializerFacade facade) {
			return new ClassSerializer(facade);
		}

	}

}
