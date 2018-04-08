package net.amygdalum.testrecorder.types;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;

public interface SerializedFieldType extends Comparable<SerializedFieldType> {

	Class<?> getDeclaringClass();

	String getName();

	Type getType();

	SerializedValue getValue();

	Annotation[] getAnnotations();

	<T extends Annotation> Optional<T> getAnnotation(Class<T> clazz);

	<T> T accept(Deserializer<T> visitor, DeserializerContext context);

    default int compareTo(SerializedFieldType o) {
        return getName().compareTo(o.getName());
    }


}