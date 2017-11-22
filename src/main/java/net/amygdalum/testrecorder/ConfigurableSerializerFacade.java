package net.amygdalum.testrecorder;

import static java.lang.System.identityHashCode;
import static net.amygdalum.testrecorder.runtime.LambdaSignature.isSerializableLambda;
import static net.amygdalum.testrecorder.util.ByteCode.classFromInternalName;
import static net.amygdalum.testrecorder.util.Reflections.accessing;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.values.SerializedLiteral.isLiteral;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import net.amygdalum.testrecorder.SerializationProfile.Excluded;
import net.amygdalum.testrecorder.runtime.LambdaSignature;
import net.amygdalum.testrecorder.serializers.ArraySerializer;
import net.amygdalum.testrecorder.serializers.EnumSerializer;
import net.amygdalum.testrecorder.serializers.GenericSerializer;
import net.amygdalum.testrecorder.serializers.LambdaSerializer;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;

public class ConfigurableSerializerFacade implements SerializerFacade {

	private Map<Class<?>, Serializer<?>> serializers;
	private Map<Object, SerializedValue> serialized;
	private List<Classes> classExclusions;
	private List<Fields> fieldExclusions;

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
			System.err.println("failed loading serializers: " + serviceError.getMessage());
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
		} else if (isLiteral(object.getClass()) && baseType(type).isPrimitive()) {
			return SerializedLiteral.literal(type, object);
		} else if (isLiteral(object.getClass())) {
			return SerializedLiteral.literal(object);
		} else if (isSerializableLambda(object.getClass())) {
			return createLambdaObject(type, object);
		} else {
			return createObject(type, object);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private SerializedValue createLambdaObject(Type type, Object object) {
		SerializedValue serializedObject = serialized.get(object);
		if (serializedObject == null) {
			SerializedLambda serializedLambda = LambdaSignature.serialize(object);
			try {
				Class<?> functionalInterfaceType = classFromInternalName(serializedLambda.getFunctionalInterfaceClass());
				Serializer serializer = fetchSerializer(serializedLambda.getClass());
				serializedObject = serializer.generate(type, functionalInterfaceType);
				serialized.put(object, serializedObject);
				if (serializedObject instanceof SerializedReferenceType) {
					((SerializedReferenceType) serializedObject).setId(identityHashCode(object));
				}
				serializer.populate(serializedObject, serializedLambda);
			} catch (ReflectiveOperationException e) {
				throw new SerializationException(e);
			}
		}
		return serializedObject;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private SerializedValue createObject(Type type, Object object) {
		SerializedValue serializedObject = serialized.get(object);
		if (serializedObject == null) {
			Serializer serializer = fetchSerializer(object.getClass());
			serializedObject = serializer.generate(type, object.getClass());
			serialized.put(object, serializedObject);
			if (serializedObject instanceof SerializedReferenceType) {
				((SerializedReferenceType) serializedObject).setId(identityHashCode(object));
			}
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
		} else if (SerializedLambda.class == clazz) {
			serializer = new LambdaSerializer(this);
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
			return accessing(field).call(f -> createField(f, obj));
		} catch (ReflectiveOperationException e) {
			throw new SerializationException(e);
		}
	}

	private SerializedField createField(Field field, Object obj) throws IllegalAccessException {
		Class<?> declaringClass = field.getDeclaringClass();
		String name = field.getName();
		Type type = field.getGenericType();
		SerializedValue serializedObject = serialize(type, field.get(obj));
		SerializedField serializedField = new SerializedField(declaringClass, name, type, serializedObject);

		return serializedField;
	}

	@Override
	public boolean excludes(Field field) {
		if (field.isAnnotationPresent(Excluded.class)) {
			return true;
		}
		boolean excluded = fieldExclusions.stream()
			.anyMatch(exclusion -> exclusion.matches(field));
		if (!excluded) {
			Class<?> type = field.getType();
			excluded = classExclusions.stream()
				.anyMatch(exclusion -> exclusion.matches(type));
		}
		return excluded;
	}

	@Override
	public boolean excludes(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Excluded.class)) {
			return true;
		}
		return classExclusions.stream()
			.anyMatch(exclusion -> exclusion.matches(clazz));
	}

}
