package net.amygdalum.testrecorder;

import static java.lang.System.identityHashCode;
import static java.lang.reflect.Proxy.isProxyClass;
import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.asm.ByteCode.classFrom;
import static net.amygdalum.testrecorder.util.Reflections.accessing;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.isLiteral;
import static net.amygdalum.testrecorder.util.Types.serializableOf;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.Classes;
import net.amygdalum.testrecorder.profile.Fields;
import net.amygdalum.testrecorder.profile.SerializationProfile;
import net.amygdalum.testrecorder.serializers.ArraySerializer;
import net.amygdalum.testrecorder.serializers.EnumSerializer;
import net.amygdalum.testrecorder.serializers.GenericSerializer;
import net.amygdalum.testrecorder.serializers.LambdaSerializer;
import net.amygdalum.testrecorder.serializers.ProxySerializer;
import net.amygdalum.testrecorder.serializers.SerializerFacade;
import net.amygdalum.testrecorder.types.OverrideSerializer;
import net.amygdalum.testrecorder.types.Profile;
import net.amygdalum.testrecorder.types.SerializationException;
import net.amygdalum.testrecorder.types.SerializedReferenceType;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.util.Lambdas;
import net.amygdalum.testrecorder.util.Logger;
import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedOutput;
import net.amygdalum.testrecorder.values.SerializedPlaceholder;

public class ConfigurableSerializerFacade implements SerializerFacade {

	private Map<Class<?>, Serializer<?>> serializers;
	private Serializer<SerializedArray> arraySerializer;
	private EnumSerializer enumSerializer;
	private LambdaSerializer lambdaSerializer;
	private ProxySerializer proxySerializer;
	private GenericSerializer genericSerializer;

	private List<Classes> classExclusions;
	private List<Classes> classFacades;
	private List<Fields> fieldExclusions;
	private List<Fields> fieldFacades;

	public ConfigurableSerializerFacade(AgentConfiguration config) {
		serializers = setupSerializers(config, this);
		arraySerializer = new ArraySerializer(this);
		enumSerializer = new EnumSerializer(this);
		lambdaSerializer = new LambdaSerializer(this);
		proxySerializer = new ProxySerializer(this);
		genericSerializer = new GenericSerializer(this);
		classExclusions = classExclusions(config);
		classFacades = classFacades(config);
		fieldExclusions = fieldExclusions(config);
		fieldFacades = fieldFacades(config);
	}

	private static List<Classes> classExclusions(AgentConfiguration config) {
		List<Classes> excluded = new ArrayList<>(config.loadConfiguration(SerializationProfile.class)
			.getClassExclusions());
		excluded.addAll(testrecorderClasses());
		return excluded;
	}

	private static List<Classes> classFacades(AgentConfiguration config) {
		List<Classes> facades = new ArrayList<>(config.loadConfiguration(SerializationProfile.class)
			.getClassFacades());
		facades.addAll(testrecorderClasses());
		return facades;
	}

	private static List<Classes> testrecorderClasses() {
		return asList(
			Classes.byDescription(SnapshotManager.class),
			Classes.byDescription(ContextSnapshot.class),
			Classes.byDescription(SerializerFacade.class),
			Classes.byDescription(ConfigurableSerializerFacade.class),
			Classes.byDescription(SerializerSession.class),
			Classes.byDescription(DefaultSerializerSession.class),
			Classes.byDescription(Profile.class),
			Classes.byDescription(Logger.class),
			Classes.byPackage("net.amygdalum.testrecorder.values"));
	}

	private static List<Fields> fieldExclusions(AgentConfiguration config) {
		List<Fields> excluded = new ArrayList<>(config.loadConfiguration(SerializationProfile.class)
			.getFieldExclusions());
		return excluded;
	}

	private static List<Fields> fieldFacades(AgentConfiguration config) {
		List<Fields> facades = new ArrayList<>(config.loadConfiguration(SerializationProfile.class)
			.getFieldFacades());
		return facades;
	}

	private static Map<Class<?>, Serializer<?>> setupSerializers(AgentConfiguration config, SerializerFacade facade) {
		IdentityHashMap<Class<?>, Serializer<?>> serializers = new IdentityHashMap<>();
		for (Serializer<?> serializer : config.loadConfigurations(Serializer.class, facade)) {
			for (Class<?> clazz : serializer.getMatchingClasses()) {
				Serializer<?> existing = serializers.putIfAbsent(clazz, serializer);
				if (existing != null) {
					if (compare(serializer, existing) > 0) {
						serializers.put(clazz, serializer);
					}
				}
			}
		}
		return serializers;
	}

	private static int compare(Serializer<?> serializer1, Serializer<?> serializer2) {
		OverrideSerializer[] overrides1 = serializer1.getClass().getDeclaredAnnotationsByType(OverrideSerializer.class);
		OverrideSerializer[] overrides2 = serializer2.getClass().getDeclaredAnnotationsByType(OverrideSerializer.class);
		if (Arrays.stream(overrides1)
			.filter(o -> o.value() == serializer2.getClass())
			.findAny()
			.isPresent()) {
			return 1;
		} else if (Arrays.stream(overrides2)
			.filter(o -> o.value() == serializer1.getClass())
			.findAny()
			.isPresent()) {
			return -1;
		} else {
			return 0;
		}
	}

	@Override
	public SerializedValue serialize(Type type, Object object, SerializerSession session) {
		session.analyze(object);
		if (object == null) {
			return SerializedNull.nullInstance(serializableOf(type));
		} else if (isLiteral(object.getClass()) && baseType(type).isPrimitive()) {
			return SerializedLiteral.literal(baseType(type), object);
		} else if (isLiteral(object.getClass())) {
			return SerializedLiteral.literal(object);
		} else if (Lambdas.isSerializableLambda(object.getClass())) {
			return createLambdaObject(type, object, session);
		} else {
			return createObject(type, object, session);
		}
	}

	@Override
	public SerializedValue serializePlaceholder(Type type, Object object, SerializerSession session) {
		if (object == null) {
			return SerializedNull.nullInstance(serializableOf(type));
		} else if (isLiteral(object.getClass()) && baseType(type).isPrimitive()) {
			return SerializedLiteral.literal(baseType(type), object);
		} else if (isLiteral(object.getClass())) {
			return SerializedLiteral.literal(object);
		} else if (Lambdas.isSerializableLambda(object.getClass())) {
			return createLambdaObject(type, object, session);
		} else {
			return createPlaceholder(type, object, session);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private SerializedValue createLambdaObject(Type type, Object object, SerializerSession session) {
		Profile serialization = session.log(type);
		SerializedValue serializedObject = session.find(object);
		if (serializedObject == null) {
			SerializedLambda serializedLambda = Lambdas.serializeLambda(object);
			try {
				Class<?> functionalInterfaceType = classFrom(serializedLambda.getFunctionalInterfaceClass());
				Serializer serializer = fetchSerializer(serializedLambda.getClass());
				serializedObject = serializer.generate(serializableOf(functionalInterfaceType), session);
				session.resolve(object, serializedObject);
				if (serializedObject instanceof SerializedReferenceType) {
					SerializedReferenceType serializedReferenceType = (SerializedReferenceType) serializedObject;
					serializedReferenceType.useAs(serializableOf(functionalInterfaceType));
					serializedReferenceType.setId(identityHashCode(object));
				}
				serializer.populate(serializedObject, serializedLambda, session);
			} catch (Throwable e) {
				throw new SerializationException(e);
			}
		}
		serialization.stop();
		return serializedObject;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private SerializedValue createObject(Type type, Object object, SerializerSession session) {
		Profile serialization = session.log(type);
		SerializedValue serializedObject = session.find(object);
		if (serializedObject == null) {
			try {
				Serializer serializer = fetchSerializer(object.getClass());
				serializedObject = serializer.generate(serializableOf(object.getClass()), session);
				session.resolve(object, serializedObject);
				if (serializedObject instanceof SerializedReferenceType) {
					SerializedReferenceType serializedReferenceType = (SerializedReferenceType) serializedObject;
					serializedReferenceType.setId(identityHashCode(object));
					serializedReferenceType.useAs(serializableOf(type));
				}
				serializer.populate(serializedObject, object, session);
			} catch (Throwable e) {
				throw new SerializationException(e);
			}
		} else if (serializedObject instanceof SerializedReferenceType) {
			SerializedReferenceType serializedReferenceType = (SerializedReferenceType) serializedObject;
			serializedReferenceType.useAs(serializableOf(type));
		}
		serialization.stop();
		return serializedObject;
	}

	private SerializedValue createPlaceholder(Type type, Object object, SerializerSession session) {
		Profile serialization = session.log(type);
		SerializedValue serializedObject = session.find(object);
		if (serializedObject == null) {
			try {
				serializedObject = new SerializedPlaceholder(object.getClass());
				session.resolve(object, serializedObject);
				if (serializedObject instanceof SerializedReferenceType) {
					SerializedReferenceType serializedReferenceType = (SerializedReferenceType) serializedObject;
					serializedReferenceType.setId(identityHashCode(object));
					serializedReferenceType.useAs(serializableOf(type));
				}
			} catch (Throwable e) {
				throw new SerializationException(e);
			}
		} else if (serializedObject instanceof SerializedReferenceType) {
			SerializedReferenceType serializedReferenceType = (SerializedReferenceType) serializedObject;
			serializedReferenceType.useAs(serializableOf(type));
		}
		serialization.stop();
		return serializedObject;
	}
	
	private Serializer<?> fetchSerializer(Class<?> clazz) {
		Serializer<?> serializer = serializers.get(clazz);
		if (serializer != null) {
			return serializer;
		}
		if (clazz.isArray()) {
			return arraySerializer;
		} else if (clazz.isEnum() || (clazz.getSuperclass() != null && clazz.getSuperclass().isEnum())) {
			return enumSerializer;
		} else if (SerializedLambda.class == clazz) {
			return lambdaSerializer;
		} else if (isProxyClass(clazz)) {
			return proxySerializer;
		} else {
			return genericSerializer;
		}
	}

	@Override
	public SerializedValue[] serialize(Type[] clazzes, Object[] objects, SerializerSession session) {
		return IntStream.range(0, clazzes.length)
			.mapToObj(i -> serialize(clazzes[i], objects[i], session))
			.toArray(SerializedValue[]::new);
	}

	@Override
	public SerializedField serialize(Field field, Object obj, SerializerSession session) {
		try {
			return accessing(field).call(f -> createField(f, obj, session));
		} catch (ReflectiveOperationException e) {
			throw new SerializationException(e);
		}
	}

	@Override
	public SerializedOutput serializeOutput(int id, Class<?> clazz, String method, Type resultType, Type[] paramTypes) {
		return new SerializedOutput(id, clazz, method, serializableOf(resultType), serializableOf(paramTypes));
	}
	
	@Override
	public SerializedInput serializeInput(int id, Class<?> clazz, String method, Type resultType, Type[] paramTypes) {
		return new SerializedInput(id, clazz, method, serializableOf(resultType), serializableOf(paramTypes));
	}

	private SerializedField createField(Field field, Object obj, SerializerSession session) throws IllegalAccessException {
		Class<?> declaringClass = field.getDeclaringClass();
		String name = field.getName();
		Type type = field.getGenericType();
		Object object = field.get(obj);
		SerializedValue serializedObject = serialize(type, object, session);
		SerializedField serializedField = new SerializedField(declaringClass, name, serializableOf(type), serializedObject);

		return serializedField;
	}

	@Override
	public SerializerSession newSession() {
		return new DefaultSerializerSession()
			.withClassExclusions(classExclusions)
			.withFieldExclusions(fieldExclusions)
			.withClassFacades(classFacades)
			.withFieldFacades(fieldFacades);
	}

}
