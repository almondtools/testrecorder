package net.amygdalum.testrecorder.serializers;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.TypeFilters.startingWith;
import static net.amygdalum.testrecorder.util.Types.inferType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.typeArgument;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.util.Reflections;
import net.amygdalum.testrecorder.values.SerializedMap;

public class CollectionsMapSerializer extends HiddenInnerClassSerializer<SerializedMap> {

	public CollectionsMapSerializer() {
		super(Collections.class);
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return innerClasses().stream()
			.filter(startingWith("Unmodifiable", "Synchronized", "Checked", "Empty", "Singleton"))
			.filter(clazz -> Map.class.isAssignableFrom(clazz))
			.collect(toList());
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
		Type[] componentTypes = computeComponentType(serializedObject, object);
		Type keyType = componentTypes[0];
		Type valueType = componentTypes[1];

		for (Map.Entry<?, ?> element : ((Map<?, ?>) object).entrySet()) {
			Object key = element.getKey();
			Object value = element.getValue();
			serializedObject.put(serializedValueOf(session, keyType, key), serializedValueOf(session, valueType, value));
		}
		Type newType = parameterized(Map.class, null, componentTypes);
		serializedObject.useAs(newType);
	}

	private Type[] computeComponentType(SerializedMap serializedObject, Object object) {
		if (object.getClass().getSimpleName().contains("Checked")) {
			return new Type[] { getKeyTypeField(object), getValueTypeField(object) };
		}
		Stream<Type> keyDefinedTypes = Arrays.stream(serializedObject.getUsedTypes())
			.map(type -> typeArgument(type, 0).orElse(Object.class));
		Stream<Type> keyElementTypes = ((Map<?, ?>) object).keySet().stream()
			.filter(Objects::nonNull)
			.map(element -> element.getClass());
		Stream<Type> keyTypes = Stream.concat(keyDefinedTypes, keyElementTypes);

		Stream<Type> valueDefinedTypes = Arrays.stream(serializedObject.getUsedTypes())
			.map(type -> typeArgument(type, 1).orElse(Object.class));
		Stream<Type> valueElementTypes = ((Map<?, ?>) object).values().stream()
			.filter(Objects::nonNull)
			.map(element -> element.getClass());
		Stream<Type> valueTypes = Stream.concat(valueDefinedTypes, valueElementTypes);

		return new Type[] { inferType(keyTypes.toArray(Type[]::new)), inferType(valueTypes.toArray(Type[]::new)) };
	}

	private Class<?> getKeyTypeField(Object object) {
		try {
			return (Class<?>) Reflections.getValue("keyType", object);
		} catch (ReflectiveOperationException e) {
			return Object.class;
		}
	}

	private Class<?> getValueTypeField(Object object) {
		try {
			return (Class<?>) Reflections.getValue("valueType", object);
		} catch (ReflectiveOperationException e) {
			return Object.class;
		}
	}

}
