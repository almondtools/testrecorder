package net.amygdalum.testrecorder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import net.amygdalum.testrecorder.values.SerializedField;

public interface SerializerFacade {
    
	void reset();

    public default SerializedValue serialize(Type type, Object object) {
        return serialize(null, type, object);
    }

    SerializedValue serialize(Annotation[] annotations, Type type, Object object);

    public default SerializedValue[] serialize(Type[] clazzes, Object[] objects) {
        return serialize(null, clazzes, objects);
    }
    
	SerializedValue[] serialize(Annotation[][] annotations, Type[] clazzes, Object[] objects);

	SerializedField serialize(Field f, Object obj);

	boolean excludes(Field field);

	boolean excludes(Class<?> clazz);

}