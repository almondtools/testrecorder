package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedMap;

public class DefaultMapSerializer implements Serializer<SerializedMap> {

	private SerializerFacade facade;

	public DefaultMapSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(HashMap.class, LinkedHashMap.class, TreeMap.class);
	}

	@Override
	public SerializedMap generate(Type type) {
		return new SerializedMap(type);
	}

	@Override
	public void populate(SerializedMap serializedObject, Object object) {
		Type keyType = serializedObject.getMapKeyType();
		Type valueType = serializedObject.getMapValueType();
		for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			serializedObject.put(facade.serialize(keyType, key), facade.serialize(valueType, value));
		}
	}

}
