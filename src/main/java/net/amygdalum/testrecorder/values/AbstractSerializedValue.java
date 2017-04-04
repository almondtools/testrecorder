package net.amygdalum.testrecorder.values;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.SerializedValue;

public abstract class AbstractSerializedValue implements SerializedValue {

    private Type type;

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

}
