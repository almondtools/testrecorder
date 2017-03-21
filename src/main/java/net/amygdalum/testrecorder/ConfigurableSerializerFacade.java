package net.amygdalum.testrecorder;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.util.Reflections.accessing;
import static net.amygdalum.testrecorder.values.SerializedLiteral.isLiteral;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import net.amygdalum.testrecorder.SerializationProfile.Excluded;
import net.amygdalum.testrecorder.SerializationProfile.Hint;
import net.amygdalum.testrecorder.serializers.ArraySerializer;
import net.amygdalum.testrecorder.serializers.EnumSerializer;
import net.amygdalum.testrecorder.serializers.GenericSerializer;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;

public class ConfigurableSerializerFacade implements SerializerFacade {

    private Map<Class<?>, Serializer<?>> serializers;
    private Map<Object, SerializedValue> serialized;
    private List<Predicate<Class<?>>> classExclusions;
    private List<Predicate<Field>> fieldExclusions;

    public ConfigurableSerializerFacade(SerializationProfile profile) {
        serializers = setupSerializers(this);
        serialized = new IdentityHashMap<>();
        classExclusions = profile.getClassExclusions();
        fieldExclusions = profile.getFieldExclusions();
    }

    @SuppressWarnings("rawtypes")
    private static Map<Class<?>, Serializer<?>> setupSerializers(SerializerFacade facade) {
        IdentityHashMap<Class<?>, Serializer<?>> serializers = new IdentityHashMap<>();
        try {
            ServiceLoader<SerializerFactory> loader = ServiceLoader.load(SerializerFactory.class);

            StreamSupport.stream(loader.spliterator(), false)
                .map(factory -> (Serializer<?>) factory.newSerializer(facade))
                .forEach(serializer -> {
                    for (Class<?> clazz : serializer.getMatchingClasses()) {
                        serializers.put(clazz, serializer);
                    }
                });
        } catch (ServiceConfigurationError serviceError) {
            System.out.println("failed loading serializers: " + serviceError.getMessage());
        }

        return serializers;
    }

    @Override
    public void reset() {
        serialized.clear();
    }

    @Override
    public SerializedValue serialize(Type type, Object object) {
        if (object == null) {
            return SerializedNull.nullInstance(type);
        } else if (isLiteral(object.getClass())) {
            return SerializedLiteral.literal(object);
        }
        return createObject(type, object);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private SerializedValue createObject(Type type, Object object) {
        SerializedValue serializedObject = serialized.get(object);
        if (serializedObject == null) {
            Serializer serializer = fetchSerializer(object.getClass());
            serializedObject = serializer.generate(type, object.getClass());
            serialized.put(object, serializedObject);
            serializer.populate(serializedObject, object);
        }
        return serializedObject;
    }

    private Serializer<?> fetchSerializer(Class<?> clazz) {
        Serializer<?> serializer = serializers.get(clazz);
        if (serializer != null) {
            return serializer;
        }
        if (clazz.isArray()) {
            serializer = new ArraySerializer(this);
        } else if (clazz.isEnum() || (clazz.getSuperclass() != null && clazz.getSuperclass().isEnum())) {
            serializer = new EnumSerializer(this);
        } else {
            serializer = new GenericSerializer(this);
        }
        return serializer;
    }

    @Override
    public SerializedValue[] serialize(Type[] clazzes, Object[] objects) {
        return IntStream.range(0, clazzes.length)
            .mapToObj(i -> serialize(clazzes[i], objects[i]))
            .toArray(SerializedValue[]::new);
    }

    @Override
    public SerializedField serialize(Field field, Object obj) {
        try {
            return accessing(field).call(() -> createField(field, obj));
        } catch (ReflectiveOperationException e) {
            throw new SerializationException(e);
        }
    }

    private SerializedField createField(Field field, Object obj) throws IllegalAccessException {
        Class<?> declaringClass = field.getDeclaringClass();
        String name = field.getName();
        Class<?> type = field.getType();
        SerializedValue serializedObject = serialize(type, field.get(obj));
        SerializedField serializedField = new SerializedField(declaringClass, name, type, serializedObject);
        Hint[] hints = field.getAnnotationsByType(Hint.class);
        if (hints.length > 0) {
            serializedField.addHints(createHints(field, obj, hints));
        }
        return serializedField;
    }

    private List<DeserializationHint> createHints(Field field, Object obj, Hint[] hints) {
        if (hints.length == 0) {
            return emptyList();
        }
        List<DeserializationHint> deserializationHints = new ArrayList<>(hints.length);
        for (Hint hint : hints) {
            Class<? extends DeserializationHint> type = hint.value();
            DeserializationHint deserializationHint = constructWithContext(type, field, obj);
            if (deserializationHint == null) {
                deserializationHint = constructWithoutContext(type);
            }
            if (deserializationHint != null) {
                deserializationHints.add(deserializationHint);
            } else {
                System.out.println("failed serializing deserialization hint " + type.getSimpleName());
            }
        }
        return deserializationHints;
    }

    private DeserializationHint constructWithContext(Class<? extends DeserializationHint> type, Field field, Object obj) {
        try {
            Constructor<? extends DeserializationHint> constructor = type.getDeclaredConstructor(Field.class, Object.class);
            DeserializationHint deserializationHint = constructor.newInstance(field, obj);
            return deserializationHint;
        } catch (ReflectiveOperationException | SecurityException e) {
            return null;
        }
    }

    private DeserializationHint constructWithoutContext(Class<? extends DeserializationHint> type) {
        try {
            return type.newInstance();
        } catch (ReflectiveOperationException | SecurityException e) {
            return null;
        }
    }

    @Override
    public boolean excludes(Field field) {
        if (field.isAnnotationPresent(Excluded.class)) {
            return true;
        } 
        boolean excluded = fieldExclusions.stream()
            .anyMatch(exclusion -> exclusion.test(field));
        if (!excluded) {
            Class<?> type = field.getType();
            excluded = classExclusions.stream()
                .anyMatch(exclusion -> exclusion.test(type));
        }
        return excluded;
    }

    @Override
    public boolean excludes(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Excluded.class)) {
            return true;
        }
        return classExclusions.stream()
            .anyMatch(exclusion -> exclusion.test(clazz));
    }

}
