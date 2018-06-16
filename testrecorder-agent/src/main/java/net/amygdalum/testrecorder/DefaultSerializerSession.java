package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.asm.ByteCode.classFrom;
import static net.amygdalum.testrecorder.util.Lambdas.isSerializableLambda;
import static net.amygdalum.testrecorder.util.Reflections.accessing;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.isLiteral;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.amygdalum.testrecorder.profile.Classes;
import net.amygdalum.testrecorder.profile.Excluded;
import net.amygdalum.testrecorder.profile.Facade;
import net.amygdalum.testrecorder.profile.Fields;
import net.amygdalum.testrecorder.types.AnalyzedObject;
import net.amygdalum.testrecorder.types.Profile;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.util.Lambdas;

public class DefaultSerializerSession implements SerializerSession {

	private Map<Object, SerializedValue> serialized;
	private Map<Class<?>, Profile> profiles;

	private List<Classes> classExclusions;
	private List<Classes> classFacades;
	private List<Fields> fieldExclusions;
	private List<Fields> fieldFacades;
	private Map<Object, Object> facaded;

	public DefaultSerializerSession() {
		serialized = new IdentityHashMap<>();
		profiles = new LinkedHashMap<>();
		facaded = new IdentityHashMap<>();
		classExclusions = new ArrayList<>();
		classFacades = new ArrayList<>();
		fieldExclusions = new ArrayList<>();
		fieldFacades = new ArrayList<>();
	}

	public DefaultSerializerSession withClassExclusions(List<Classes> classExclusions) {
		this.classExclusions.addAll(classExclusions);
		return this;
	}

	public DefaultSerializerSession withClassFacades(List<Classes> classFacades) {
		this.classFacades.addAll(classFacades);
		return this;
	}

	public DefaultSerializerSession withFieldExclusions(List<Fields> fieldExclusions) {
		this.fieldExclusions.addAll(fieldExclusions);
		return this;
	}

	public DefaultSerializerSession withFieldFacades(List<Fields> fieldFacades) {
		this.fieldFacades.addAll(fieldFacades);
		return this;
	}

	@Override
	public synchronized Profile log(Type type) {
		return profiles.computeIfAbsent(baseType(type), (t) -> Profile.start(t));
	}

	@Override
	public synchronized List<Profile> dumpProfiles() {
		List<Profile> dump = profiles.values().stream()
			.sorted()
			.limit(20)
			.collect(toList());
		profiles = new LinkedHashMap<>();
		return dump;
	}

	@Override
	public SerializedValue find(Object object) {
		return serialized.get(object);
	}

	@Override
	public void resolve(Object object, SerializedValue value) {
		serialized.put(object, value);
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

	public AnalyzedObject analyze(Object object) {
		if (object == null) {
			return new AnalyzedObject(null);
		}
		Class<?> clazz = object.getClass();
		if (isLiteral(clazz)) {
			return new AnalyzedObject(clazz, object);
		} 
		if (isSerializableLambda(clazz)) {
			SerializedLambda lambda = Lambdas.serializeLambda(object);
			Class<?> type = classFrom(lambda.getFunctionalInterfaceClass());
			return new AnalyzedObject(object, type, lambda);
		}
		if (facades(clazz)) {
			facaded.put(object, object);
		}
		Class<?> objectClass = clazz;
		while (objectClass != Object.class) {
			for (Field field : objectClass.getDeclaredFields()) {
				if (facades(field)) {
					try {
						Object obj = accessing(field).call(f -> f.get(object));
						facaded.put(obj, obj);
					} catch (ReflectiveOperationException e) {
						continue;
					}
				}
			}
			objectClass = objectClass.getSuperclass();
		}
		return new AnalyzedObject(clazz, object);
	}

	private boolean facades(Field field) {
		if (field.isAnnotationPresent(Facade.class)) {
			return true;
		}
		return fieldFacades.stream()
			.anyMatch(facade -> facade.matches(field));
	}

	private boolean facades(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Facade.class)) {
			return true;
		}
		return classFacades.stream()
			.anyMatch(facade -> facade.matches(clazz));
	}

	@Override
	public boolean excludes(Class<?> clazz) {
		if (clazz.isAnnotationPresent(Excluded.class)) {
			return true;
		}
		return classExclusions.stream()
			.anyMatch(exclusion -> exclusion.matches(clazz));
	}

	@Override
	public boolean facades(Object object) {
		return facaded.containsKey(object);
	}
}
