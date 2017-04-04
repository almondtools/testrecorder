package net.amygdalum.testrecorder;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import net.amygdalum.testrecorder.values.SerializedField;

public interface SerializerFacade {
    
	void reset();

    SerializedValue serialize(Type type, Object object);

	SerializedValue[] serialize(Type[] clazzes, Object[] objects);

	SerializedField serialize(Field f, Object obj);

	boolean excludes(Field field);

	boolean excludes(Class<?> clazz);

}