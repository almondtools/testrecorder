package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.emptyList;

import java.lang.reflect.Array;
import java.util.List;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedArray;

public class ArraySerializer implements Serializer<SerializedArray> {

	private SerializerFacade facade;

	public ArraySerializer(SerializerFacade facade) {
		this.facade = facade;
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
		for (int i = 0; i < Array.getLength(object); i++) {
			serializedObject.add(facade.serialize(serializedObject.getComponentType(), Array.get(object, i), session));
		}
	}

}
