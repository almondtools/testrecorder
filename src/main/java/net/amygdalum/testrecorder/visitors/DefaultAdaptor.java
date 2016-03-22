package net.amygdalum.testrecorder.visitors;

import net.amygdalum.testrecorder.SerializedValue;

public abstract class DefaultAdaptor<T extends SerializedValue,G> implements Adaptor<T,G> {

	@Override
	public boolean matches(Class<?> clazz) {
		return true;
	}

	@Override
	public Class<? extends Adaptor<T,G>> parent() {
		return null;
	}
	
}
