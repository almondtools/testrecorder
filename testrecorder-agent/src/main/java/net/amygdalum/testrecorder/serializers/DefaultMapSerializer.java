package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedMap;

public class DefaultMapSerializer extends AbstractCompositeSerializer implements Serializer<SerializedMap> {

	public DefaultMapSerializer() {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(HashMap.class, LinkedHashMap.class, TreeMap.class);
	}

	@Override
	public Stream<?> components(Object object, SerializerSession session) {
		return ((Map<?, ?>) object).entrySet().stream()
			.flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()));
	}

	@Override
	public SerializedMap generate(Class<?> type, SerializerSession session) {
		return new SerializedMap(type);
	}

	@Override
	public void populate(SerializedMap serializedObject, Object object, SerializerSession session) {
		for (Map.Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
			Object key = entry.getKey();
			Object value = entry.getValue();
			serializedObject.put(resolvedValueOf(session, serializedObject.getMapKeyType(), key), resolvedValueOf(session, serializedObject.getMapValueType(), value));
		}
	}

}
