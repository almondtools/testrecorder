package net.amygdalum.testrecorder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;

import net.amygdalum.testrecorder.types.SerializedValue;

public class AnnotatedValue {
    public Type type;
	public Annotation[] annotations;
    public SerializedValue value;
    
    public AnnotatedValue(Type type, Annotation[] annotations, SerializedValue value) {
    	this.type = type;
        this.annotations = annotations;
        this.value = value;
    }
    
    public <T extends Annotation> Optional<T> getAnnotation(Class<T> clazz) {
        for (int i = 0; i < annotations.length; i++) {
            if (clazz.isInstance(annotations[i])) {
                return Optional.of(clazz.cast(annotations[i]));
            }
        }
        return Optional.empty();
    }
    
}