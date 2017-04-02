package net.amygdalum.testrecorder.values;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import net.amygdalum.testrecorder.SerializedValue;

public abstract class AbstractSerializedValue implements SerializedValue {

    private Type type;
    private Annotation[] hints;

    public AbstractSerializedValue(Type type) {
        this.type = type;
    }

    @Override
    public Type getResultType() {
        return type;
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
    public void addHints(Annotation[] hints) {
        this.hints = hints;
    }

    @Override
    public Annotation[] getHints() {
        return hints;
    }

}
