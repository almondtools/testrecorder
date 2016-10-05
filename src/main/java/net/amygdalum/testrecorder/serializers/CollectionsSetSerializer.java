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
import java.util.Set;

import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.SerializerFactory;
import net.amygdalum.testrecorder.values.SerializedSet;

public class CollectionsSetSerializer extends HiddenInnerClassSerializer<SerializedSet> {

	public CollectionsSetSerializer(SerializerFacade facade) {
		super(Collections.class, facade);
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return innerClasses()
			.filter(startingWith("Unmodifiable", "Synchronized", "Checked", "Empty", "Singleton"))
			.filter(clazz -> Set.class.isAssignableFrom(clazz))
			.collect(toList());
	}

	@Override
	public SerializedSet generate(Type resultType, Type type) {
		return new SerializedSet(type).withResult(resultType);
	}

	@Override
	public void populate(SerializedSet serializedObject, Object object) {
		List<Type> elementTypes = new ArrayList<>();
		for (Object element : (Set<?>) object) {
			serializedObject.add(facade.serialize(element.getClass(), element));
			if (element != null) {
				elementTypes.add(element.getClass());
			}
		}
		if (object.getClass().getSimpleName().contains("Checked")) {
			Type newType = parameterized(Set.class, null, xray(object).to(CheckedSet.class).getType());
			serializedObject.setResultType(newType);
		} else {
			Type newType = parameterized(Set.class, null, inferType(elementTypes));
			serializedObject.setResultType(newType);
		}
	}

	interface CheckedSet {
		Class<?> getType();
	}

	public static class Factory implements SerializerFactory<SerializedSet> {

		@Override
		public CollectionsSetSerializer newSerializer(SerializerFacade facade) {
			return new CollectionsSetSerializer(facade);
		}

	}

}
