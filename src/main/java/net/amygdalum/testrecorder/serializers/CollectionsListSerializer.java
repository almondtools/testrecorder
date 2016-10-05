package net.amygdalum.testrecorder.serializers;

import static net.amygdalum.xrayinterface.XRayInterface.xray;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.TypeSelector.startingWith;
import static net.amygdalum.testrecorder.util.Types.inferType;
import static net.amygdalum.testrecorder.util.Types.parameterized;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.SerializerFactory;
import net.amygdalum.testrecorder.values.SerializedList;

public class CollectionsListSerializer extends HiddenInnerClassSerializer<SerializedList> {

	public CollectionsListSerializer(SerializerFacade facade) {
		super(Collections.class, facade);
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return innerClasses()
			.filter(startingWith("Unmodifiable", "Synchronized", "Checked", "Empty", "Singleton"))
			.filter(clazz -> List.class.isAssignableFrom(clazz))
			.collect(toList());
	}

	@Override
	public SerializedList generate(Type resultType, Type type) {
		return new SerializedList(type).withResult(resultType);
	}

	@Override
	public void populate(SerializedList serializedObject, Object object) {
		List<Type> elementTypes = new ArrayList<>();
		for (Object element : (List<?>) object) {
			serializedObject.add(facade.serialize(element.getClass(), element));
			if (element != null) {
				elementTypes.add(element.getClass());
			}
		}
		if (object.getClass().getSimpleName().contains("Checked")) {
			Type newType = parameterized(List.class, null, xray(object).to(CheckedList.class).getType());
			serializedObject.setResultType(newType);
		} else {
			Type newType = parameterized(List.class, null, inferType(elementTypes));
			serializedObject.setResultType(newType);
		}
	}
	
	interface CheckedList {
		Class<?> getType();
	}

	public static class Factory implements SerializerFactory<SerializedList> {

		@Override
		public CollectionsListSerializer newSerializer(SerializerFacade facade) {
			return new CollectionsListSerializer(facade);
		}

	}

}
