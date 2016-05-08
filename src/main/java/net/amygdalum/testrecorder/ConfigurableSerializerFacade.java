package net.amygdalum.testrecorder;

import static net.amygdalum.testrecorder.util.Reflections.accessing;
import static net.amygdalum.testrecorder.values.SerializedLiteral.isLiteral;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;

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
		serializers = setupSerializers(this, profile.getSerializerFactories());
		serialized = new IdentityHashMap<>();
		classExclusions = profile.getClassExclusions();
		fieldExclusions = profile.getFieldExclusions();
	}

	private static Map<Class<?>, Serializer<?>> setupSerializers(SerializerFacade facade, List<SerializerFactory<?>> serializerFactories) {
		IdentityHashMap<Class<?>, Serializer<?>> serializers = new IdentityHashMap<>();
		serializerFactories.stream()
			.map(factory -> factory.newSerializer(facade))
			.forEach(serializer -> {
				for (Class<?> clazz : serializer.getMatchingClasses()) {
					serializers.put(clazz, serializer);
				}
			});
		return serializers;
	}

	@Override
	public void reset() {
		serialized.clear();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public SerializedValue serialize(Type type, Object object) {
		if (object == null) {
			return SerializedNull.nullInstance(type);
		} else if (isLiteral(object.getClass())) {
			return SerializedLiteral.literal(object);
		}
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
			return accessing(field).call(() -> new SerializedField(field.getDeclaringClass(), field.getName(), field.getType(), serialize(field.getType(), field.get(obj))));
		} catch (ReflectiveOperationException e) {
			System.out.println(field.getName());
			throw new SerializationException(e);
		}
	}

	@Override
	public boolean excludes(Field field) {
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
		return classExclusions.stream()
			.anyMatch(exclusion -> exclusion.test(clazz));
	}

}
