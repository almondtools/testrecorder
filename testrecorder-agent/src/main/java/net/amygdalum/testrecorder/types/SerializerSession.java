package net.amygdalum.testrecorder.types;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

public interface SerializerSession {

	Profile log(Type type);

	List<Profile> dumpProfiles();

	SerializedValue find(Object component);

	void resolve(Object object, SerializedValue value);

	SerializedValue ref(Object object, Type type);

	boolean excludes(Field field);

	boolean excludes(Class<?> clazz);

	AnalyzedObject analyze(Object object);

	boolean facades(Object object);


}
