package net.amygdalum.testrecorder.serializers;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.TypeFilters.startingWith;
import static net.amygdalum.testrecorder.util.Types.inferType;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.Types.typeArgument;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.amygdalum.testrecorder.util.Reflections;
import net.amygdalum.testrecorder.values.SerializedSet;

public class CollectionsSetSerializer extends HiddenInnerClassSerializer<SerializedSet> {

    public CollectionsSetSerializer(SerializerFacade facade) {
        super(Collections.class, facade);
    }

    @Override
    public List<Class<?>> getMatchingClasses() {
        return innerClasses().stream()
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
        Type componentType = computeComponentType(serializedObject, object);

        for (Object element : (Set<?>) object) {
            Type elementType = element != null ? element.getClass() : componentType;

            serializedObject.add(facade.serialize(elementType, element));
        }
        Type newType = parameterized(Set.class, null, componentType);
        serializedObject.setResultType(newType);
    }

    private Type computeComponentType(SerializedSet serializedObject, Object object) {
        if (object.getClass().getSimpleName().contains("Checked")) {
            return getTypeField(object);
        }
        Type resultType = serializedObject.getResultType();

        List<Type> elementTypes = new ArrayList<>();
        elementTypes.add(typeArgument(resultType, 0).orElse(Object.class));
        ((Set<?>) object).stream()
            .filter(Objects::nonNull)
            .map(element -> element.getClass())
            .forEach(elementTypes::add);

        return inferType(elementTypes);
    }

    private Class<?> getTypeField(Object object) {
        try {
        	return (Class<?>) Reflections.getValue("type", object);
        } catch (ReflectiveOperationException e) {
            return Object.class;
        }
    }

    public static class Factory implements SerializerFactory<SerializedSet> {

        @Override
        public CollectionsSetSerializer newSerializer(SerializerFacade facade) {
            return new CollectionsSetSerializer(facade);
        }

    }

}
