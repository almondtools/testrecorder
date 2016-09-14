package net.amygdalum.testrecorder.deserializers;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.DeserializationException;
import net.amygdalum.testrecorder.SerializedValue;

public interface Adaptor<T extends SerializedValue, G> {

	Class<? extends Adaptor<T,G>> parent();
	
	Class<? extends SerializedValue> getAdaptedClass();
	
	boolean matches(Type type);
	
	Computation tryDeserialize(T value, G generator) throws DeserializationException;


}
