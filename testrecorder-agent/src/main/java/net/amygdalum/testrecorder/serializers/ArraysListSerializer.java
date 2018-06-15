package net.amygdalum.testrecorder.serializers;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.TypeFilters.in;

import java.util.Arrays;
import java.util.List;

import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedList;

public class ArraysListSerializer extends HiddenInnerClassSerializer<SerializedList> {

	public ArraysListSerializer(SerializerFacade facade) {
		super(Arrays.class, facade);
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return innerClasses().stream()
			.filter(in("ArrayList"))
			.filter(clazz -> List.class.isAssignableFrom(clazz))
			.collect(toList());
	}

	@Override
	public SerializedList generate(Class<?> type, SerializerSession session) {
		return new SerializedList(type);
	}

	@Override
	public void populate(SerializedList serializedObject, Object object, SerializerSession session) {
		for (Object element : (List<?>) object) {
			serializedObject.add(facade.serialize(element.getClass(), element, session));
		}
	}

}
