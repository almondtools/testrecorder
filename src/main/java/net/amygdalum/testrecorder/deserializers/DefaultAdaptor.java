package net.amygdalum.testrecorder.deserializers;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.SerializedReferenceType;
import net.amygdalum.testrecorder.SerializedValue;

public abstract class DefaultAdaptor<T extends SerializedValue,G> implements Adaptor<T,G> {

	@Override
	public boolean matches(Type type) {
		return true;
	}

	@Override
	public Class<? extends Adaptor<T,G>> parent() {
		return null;
	}

    public SerializedValue withResultType(SerializedValue value, Type type) {
        if (value instanceof SerializedReferenceType) {
            ((SerializedReferenceType) value).setResultType(type);
        }
        return value;
    }

}
