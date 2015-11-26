package com.almondtools.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.almondtools.testrecorder.Serializer;
import com.almondtools.testrecorder.SerializerFacade;
import com.almondtools.testrecorder.SerializerFactory;
import com.almondtools.testrecorder.values.SerializedMap;

public class DefaultHashMapSerializer implements Serializer<SerializedMap> {

	private SerializerFacade facade;

	public DefaultHashMapSerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(HashMap.class, LinkedHashMap.class);
	}

	@Override
	public SerializedMap generate(Type type, Class<?> valueType) {
		return new SerializedMap(type, valueType);
	}

	@Override
	public void populate(SerializedMap serializedObject, Object object) {
		for (Map.Entry<?,?> entry : ((Map<?,?>) object).entrySet()) {
			Object key = entry.getKey(); 
			Object value = entry.getValue();
			serializedObject.put(facade.serialize(key.getClass(), key), facade.serialize(value.getClass(), value));
		}
	}

	public static class Factory implements SerializerFactory<SerializedMap> {

		@Override
		public DefaultHashMapSerializer newSerializer(SerializerFacade facade) {
			return new DefaultHashMapSerializer(facade);
		}

	}

}
