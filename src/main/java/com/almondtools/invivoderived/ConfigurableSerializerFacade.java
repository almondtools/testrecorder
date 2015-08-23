package com.almondtools.invivoderived;

import static java.util.Arrays.asList;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.almondtools.invivoderived.serializers.ArrayListSerializer;
import com.almondtools.invivoderived.serializers.ArraySerializer;
import com.almondtools.invivoderived.serializers.GenericSerializer;
import com.almondtools.invivoderived.serializers.LinkedHashMapSerializer;
import com.almondtools.invivoderived.serializers.LinkedHashSetSerializer;
import com.almondtools.invivoderived.values.SerializedField;
import com.almondtools.invivoderived.values.SerializedLiteral;
import com.almondtools.invivoderived.values.SerializedNull;

public class ConfigurableSerializerFacade implements SerializerFacade {

	public static final List<SerializerFactory> DEFAULT_SERIALIZERS = asList(
		ArrayListSerializer::new,
		LinkedHashSetSerializer::new,
		LinkedHashMapSerializer::new);

	private Map<Class<?>, Serializer> serializers;
	private Map<Object, SerializedValue> serialized;

	public ConfigurableSerializerFacade() {
		this(DEFAULT_SERIALIZERS);
	}

	public ConfigurableSerializerFacade(List<SerializerFactory> serializerFactories) {
		serializers = setupSerializers(this, serializerFactories);
		serialized = new IdentityHashMap<>();
	}

	private static Map<Class<?>, Serializer> setupSerializers(SerializerFacade facade, List<SerializerFactory> serializerFactories) {
		IdentityHashMap<Class<?>, Serializer> serializers = new IdentityHashMap<>();
		serializerFactories.stream()
			.map(factory -> factory.newFacade(facade))
			.forEach(serializer -> {
				for (Class<?> clazz : serializer.getMatchingClasses()) {
					serializers.put(clazz, serializer);
				}
			});
		return serializers;
	}

	@Override
	public SerializedValue serialize(Type type, Object object) {
		if (object == null) {
			return SerializedNull.of(type);
		} else if (isLiteral(type)) {
			return SerializedLiteral.of(type, object);
		}
		SerializedValue serializedObject = serialized.get(object);
		if (serializedObject == null) {
			Serializer serializer = fetchSerializer(type);
			serializedObject = serializer.generate(type);
			serialized.put(object, serializedObject);
			serializer.populate(serializedObject, object);
		}
		return serializedObject;
	}

	private Serializer fetchSerializer(Type type) {
		Class<?> clazz = getClass(type);
		Serializer serializer = serializers.get(clazz);
		if (serializer != null) {
			return serializer;
		}
		if (clazz.isArray()) {
			serializer = new ArraySerializer(this);
		} else {
			serializer = new GenericSerializer(this);
		}
		return serializer;
	}

	@Override
	public SerializedValue[] serialize(Type[] clazzes, Object[] objects) {
		return IntStream.range(0, clazzes.length)
			.mapToObj(i -> serialize(clazzes[i], objects[i]))
			.toArray(len -> new SerializedValue[len]);
	}

	@Override
	public SerializedField serialize(Field f, Object obj) {
		try {
			boolean access = f.isAccessible();
			if (!access) {
				f.setAccessible(true);
			}
			SerializedField field = new SerializedField(f.getName(), f.getType(), serialize(f.getType(), f.get(obj)));
			if (!access) {
				f.setAccessible(false);
			}
			return field;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			System.out.println(f.getName());
			throw new SerializationException(e);
		}
	}

	private boolean isLiteral(Type type) {
		return LITERAL_TYPES.contains(type);
	}

	private Class<?> getClass(Type type) {
		if (type instanceof Class<?>) {
			return ((Class<?>) type);
		} else if (type instanceof GenericArrayType) {
			return Array.newInstance(getClass(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
		} else if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		} else {
			return Object.class;
		}
	}

	@Override
	public boolean excludes(Class<?> type) {
		return false;
	}

}
