package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.util.Types.baseType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;

import net.amygdalum.testrecorder.types.SerializedValue;

public abstract class AbstractSerializedValue implements SerializedValue {

    private Type type;

    public AbstractSerializedValue(Type type) {
        this.type = type;
    }

    @Override
    public Type[] getUsedTypes() {
        return new Type[] {type};
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public Annotation[] getAnnotations() {
        return baseType(type).getAnnotations();
    }

    @Override
    public <T extends Annotation> Optional<T> getAnnotation(Class<T> clazz) {
        Annotation[] annotations = getAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            if (clazz.isInstance(annotations[i])) {
                return Optional.of(clazz.cast(annotations[i]));
            }
        }
        return Optional.empty();
    }

}
