package net.amygdalum.testrecorder.serializers;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.TypeFilters.in;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedList;

public class ArraysListSerializer extends HiddenInnerClassSerializer<SerializedList> {

	public ArraysListSerializer() {
		super(Arrays.class);
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return innerClasses().stream()
			.filter(in("ArrayList"))
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
		for (Object element : (List<?>) object) {
			serializedObject.add(resolvedValueOf(session, serializedObject.getComponentType(), element));
		}
	}

}
