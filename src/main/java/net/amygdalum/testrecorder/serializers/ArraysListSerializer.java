package net.amygdalum.testrecorder.serializers;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.SerializerFactory;
import net.amygdalum.testrecorder.values.SerializedList;

public class ArraysListSerializer extends HiddenInnerClassSerializer<SerializedList> {

	public ArraysListSerializer(SerializerFacade facade) {
		super(Collections.class, facade);
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return innerClasses()
			.filter(in("ArrayList"))
			.filter(clazz -> List.class.isAssignableFrom(clazz))
			.collect(toList());
	}

	@Override
	public SerializedList generate(Type type, Class<?> valueType) {
		return new SerializedList(type, valueType);
	}

	@Override
	public void populate(SerializedList serializedObject, Object object) {
		for (Object element : (List<?>) object) {
			serializedObject.add(facade.serialize(element.getClass(), element));
		}
	}

	public static class Factory implements SerializerFactory<SerializedList> {

		@Override
		public ArraysListSerializer newSerializer(SerializerFacade facade) {
			return new ArraysListSerializer(facade);
		}

	}

}
