package net.amygdalum.testrecorder.deserializers;

import net.amygdalum.testrecorder.types.DeserializationException;

public interface LocalVariableDefinition {

	Computation define(LocalVariable local) throws DeserializationException;
	
}
