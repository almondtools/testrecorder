package com.almondtools.invitroderivatives;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import com.almondtools.invitroderivatives.serializers.ArrayListSerializer;
import com.almondtools.invitroderivatives.serializers.ArraySerializer;
import com.almondtools.invitroderivatives.serializers.GenericSerializer;
import com.almondtools.invitroderivatives.serializers.LinkedHashMapSerializer;
import com.almondtools.invitroderivatives.serializers.LinkedHashSetSerializer;
import com.almondtools.invitroderivatives.values.SerializedField;
import com.almondtools.invitroderivatives.values.SerializedLiteral;
import com.almondtools.invitroderivatives.values.SerializedNull;

public class SerializerFacade {

	private static final Set<Class<?>> LITERAL_TYPES = new HashSet<>(Arrays.asList(
		boolean.class, char.class, byte.class, short.class, int.class, float.class, long.class, double.class,
		Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Float.class, Long.class, Double.class,
		String.class));
	private Map<Class<?>, Serializer> serializers;

	private Map<Object, SerializedValue> serialized;

	public SerializerFacade() {
		serializers = setupSerializers(this);
		serialized = new IdentityHashMap<>();
	}

	private static Map<Class<?>, Serializer> setupSerializers(SerializerFacade facade) {
		IdentityHashMap<Class<?>, Serializer> serializers = new IdentityHashMap<>();
		asList(
			new ArrayListSerializer(facade), 
			new LinkedHashSetSerializer(facade),
			new LinkedHashMapSerializer(facade)).stream()
			.forEach(serializer -> {
				for (Class<?> clazz : serializer.getMatchingClasses()) {
					serializers.put(clazz, serializer);
				}
			});
		return serializers;
	}

	public SerializedValue serialize(Class<?> clazz, Object object) {
		if (object == null) {
			return SerializedNull.of(clazz);
		} else if (isLiteral(clazz)) {
			return SerializedLiteral.of(clazz, object);
		}
		SerializedValue serializedObject = serialized.get(object);
		if (serializedObject == null) {
			Serializer serializer = fetchSerializer(clazz, object);
			serializedObject = serializer.generate();
			serialized.put(object, serializedObject);
			serializer.populate(serializedObject, object);
		}
		return serializedObject;
	}

	private Serializer fetchSerializer(Class<?> clazz, Object object) {
		Serializer serializer = serializers.get(clazz);
		if (serializer != null) {
			return serializer;
		}
		if (clazz.isArray()) {
			serializer = new ArraySerializer(this, clazz);
		} else {
			serializer = new GenericSerializer(this, object.getClass());
		}
		return serializer;
	}

	public SerializedValue[] serialize(Class<?>[] clazzes, Object[] objects) {
		return IntStream.range(0, clazzes.length)
			.mapToObj(i -> serialize(clazzes[i], objects[i]))
			.toArray(len -> new SerializedValue[len]);
	}

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

	private boolean isLiteral(Class<?> clazz) {
		return LITERAL_TYPES.contains(clazz);
	}

}
