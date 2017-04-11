package net.amygdalum.testrecorder.serializers;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.TypeFilters.startingWith;
import static net.amygdalum.testrecorder.util.Types.inferType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.typeArgument;
import static net.amygdalum.xrayinterface.XRayInterface.xray;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
		return innerClasses().stream()
			.filter(startingWith("Unmodifiable", "Synchronized", "Checked", "Empty", "Singleton"))
			.filter(clazz -> Map.class.isAssignableFrom(clazz))
			.collect(toList());
	}

	@Override
	public SerializedMap generate(Type resultType, Type type) {
		return new SerializedMap(type).withResult(resultType);
	}

	@Override
	public void populate(SerializedMap serializedObject, Object object) {
		List<Type> keyTypes = new ArrayList<>();
		List<Type> valueTypes = new ArrayList<>();
		Type resultType = serializedObject.getResultType();
		keyTypes.add(typeArgument(resultType, 0).orElse(Object.class));
        valueTypes.add(typeArgument(resultType, 1).orElse(Object.class));
		for (Map.Entry<?, ?> element : ((Map<?, ?>) object).entrySet()) {
			Object key = element.getKey();
			Object value = element.getValue();
			serializedObject.put(facade.serialize(key.getClass(), key), facade.serialize(value.getClass(), value));
			if (key != null) {
				keyTypes.add(key.getClass());
			}
			if (value != null) {
				valueTypes.add(value.getClass());
			}
		}
		if (object.getClass().getSimpleName().contains("Checked")) {
			Type newType = parameterized(Map.class, null, xray(object).to(CheckedMap.class).getKeyType(), xray(object).to(CheckedMap.class).getValueType());
			serializedObject.setResultType(newType);
		} else {
			Type newType = parameterized(Map.class, null, inferType(keyTypes), inferType(valueTypes));
			serializedObject.setResultType(newType);
		}
	}

	interface CheckedMap {
		Class<?> getKeyType();
		Class<?> getValueType();
	}

	public static class Factory implements SerializerFactory<SerializedMap> {

		@Override
		public CollectionsMapSerializer newSerializer(SerializerFacade facade) {
			return new CollectionsMapSerializer(facade);
		}

	}

}
