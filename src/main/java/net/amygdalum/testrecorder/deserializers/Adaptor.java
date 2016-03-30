package net.amygdalum.testrecorder.deserializers;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.SerializedValue;

public interface Adaptor<T extends SerializedValue, G> {

	Class<? extends Adaptor<T,G>> parent();
	
	boolean matches(Class<?> clazz);

	Computation tryDeserialize(T value, G generator) throws DeserializationException;

}
