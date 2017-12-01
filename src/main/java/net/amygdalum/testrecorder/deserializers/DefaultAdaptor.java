package net.amygdalum.testrecorder.deserializers;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.types.SerializedValue;

public abstract class DefaultAdaptor<T extends SerializedValue,G> implements Adaptor<T,G> {

	@Override
	public boolean matches(Type type) {
		return true;
	}

	@Override
	public Class<? extends Adaptor<T,G>> parent() {
		return null;
	}

}
