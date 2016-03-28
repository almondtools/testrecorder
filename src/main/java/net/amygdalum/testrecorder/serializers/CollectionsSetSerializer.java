package net.amygdalum.testrecorder.serializers;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.SerializerFactory;
import net.amygdalum.testrecorder.values.SerializedSet;

public class CollectionsSetSerializer extends HiddenInnerClassSerializer<SerializedSet> {

	public CollectionsSetSerializer(SerializerFacade facade) {
		super(Collections.class, facade);
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return innerClasses()
			.filter(startingWith("Unmodifiable","Synchronized","Checked","Empty","Singleton"))
			.filter(clazz -> Set.class.isAssignableFrom(clazz))
			.collect(toList());
	}

	@Override
	public SerializedSet generate(Type type, Class<?> valueType) {
		return new SerializedSet(type, valueType);
	}

	@Override
	public void populate(SerializedSet serializedObject, Object object) {
		for (Object element : (Set<?>) object) {
			serializedObject.add(facade.serialize(element.getClass(), element));
		}
	}

	public static class Factory implements SerializerFactory<SerializedSet> {

		@Override
		public CollectionsSetSerializer newSerializer(SerializerFacade facade) {
			return new CollectionsSetSerializer(facade);
		}

	}

}
