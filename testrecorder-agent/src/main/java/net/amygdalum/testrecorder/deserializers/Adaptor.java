package net.amygdalum.testrecorder.deserializers;

import java.lang.reflect.Type;

import net.amygdalum.testrecorder.types.Computation;
import net.amygdalum.testrecorder.types.DeserializationException;
import net.amygdalum.testrecorder.types.SerializedValue;

public interface Adaptor<T extends SerializedValue> {

	Class<? extends Adaptor<T>> parent();
	
	Class<? extends SerializedValue> getAdaptedClass();
	
	boolean matches(Type type);
	
	Computation tryDeserialize(T value, Deserializer generator) throws DeserializationException;

}
