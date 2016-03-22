package net.amygdalum.testrecorder.visitors;

import net.amygdalum.testrecorder.SerializedValue;

public interface Adaptor<T extends SerializedValue, G> {

	Class<? extends Adaptor<T,G>> parent();
	
	boolean matches(Class<?> clazz);

	Computation tryDeserialize(T value, TypeManager types, G generator) throws DeserializationException;

}
