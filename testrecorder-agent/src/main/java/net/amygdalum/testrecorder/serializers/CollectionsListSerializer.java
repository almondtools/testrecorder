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
import java.util.Objects;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.util.Reflections;
import net.amygdalum.testrecorder.values.SerializedList;

public class CollectionsListSerializer extends HiddenInnerClassSerializer<SerializedList> {

	public CollectionsListSerializer() {
		super(Collections.class);
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return innerClasses().stream()
			.filter(startingWith("Unmodifiable", "Synchronized", "Checked", "Empty", "Singleton"))
			.filter(clazz -> List.class.isAssignableFrom(clazz))
			.collect(toList());
	}
	
	@Override
	public Stream<?> components(Object object, SerializerSession session) {
		return ((List<?>) object).stream();
	}

	@Override
	public SerializedList generate(Class<?> type, SerializerSession session) {
		return new SerializedList(type);
	}

	@Override
	public void populate(SerializedList serializedObject, Object object, SerializerSession session) {
		Type componentType = computeComponentType(serializedObject, object);

		for (Object element : (List<?>) object) {
			serializedObject.add(serializedValueOf(session, componentType, element));
		}
		serializedObject.useAs(parameterized(List.class, null, componentType));
	}

	private Type computeComponentType(SerializedList serializedObject, Object object) {
		if (object.getClass().getSimpleName().contains("Checked")) {
			return getTypeField(object);
		}
		Stream<Type> definedTypes = Arrays.stream(serializedObject.getUsedTypes())
			.map(type -> typeArgument(type, 0).orElse(Object.class));
		Stream<Type> elementTypes = ((List<?>) object).stream()
			.filter(Objects::nonNull)
			.map(element -> element.getClass());
		Stream<Type> usedTypes = Stream.concat(definedTypes, elementTypes);
		
		return inferType(usedTypes.toArray(Type[]::new));
	}

	private Class<?> getTypeField(Object object) {
		try {
			return (Class<?>) Reflections.getValue("type", object);
		} catch (ReflectiveOperationException e) {
			return Object.class;
		}
	}

}
