package net.amygdalum.testrecorder.serializers;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.SerializerFactory;
import net.amygdalum.testrecorder.values.SerializedMap;

public class CollectionsMapSerializer extends HiddenInnerClassSerializer<SerializedMap> {

	public CollectionsMapSerializer(SerializerFacade facade) {
		super(Collections.class, facade);
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return innerClasses()
			.filter(startingWith("Unmodifiable", "Synchronized", "Checked", "Empty", "Singleton"))
			.filter(clazz -> Map.class.isAssignableFrom(clazz))
			.collect(toList());
	}

	@Override
	public SerializedMap generate(Type type, Class<?> valueType) {
		return new SerializedMap(type, valueType);
	}

	@Override
	public void populate(SerializedMap serializedObject, Object object) {
		for (Map.Entry<?, ?> element : ((Map<?, ?>) object).entrySet()) {
			Object key = element.getKey();
			Object value = element.getValue();
			serializedObject.put(facade.serialize(key.getClass(), key), facade.serialize(value.getClass(), value));
		}
	}

	public static class Factory implements SerializerFactory<SerializedMap> {

		@Override
		public CollectionsMapSerializer newSerializer(SerializerFacade facade) {
			return new CollectionsMapSerializer(facade);
		}

	}

}
