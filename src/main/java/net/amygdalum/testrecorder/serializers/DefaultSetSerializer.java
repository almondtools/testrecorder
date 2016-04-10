package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.SerializerFactory;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetSerializer implements Serializer<SerializedSet>{

	private SerializerFacade facade;

	public DefaultSetSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(HashSet.class, LinkedHashSet.class, TreeSet.class);
	}

	@Override
	public SerializedSet generate(Type resultType, Type type) {
		return new SerializedSet(type).withResult(resultType);
	}

	@Override
	public void populate(SerializedSet serializedObject, Object object) {
		for (Object element : (Set<?>) object) {
			serializedObject.add(facade.serialize(element.getClass(), element));
		}
	}

	public static class Factory implements SerializerFactory<SerializedSet> {

		@Override
		public DefaultSetSerializer newSerializer(SerializerFacade facade) {
			return new DefaultSetSerializer(facade);
		}

	}

}
