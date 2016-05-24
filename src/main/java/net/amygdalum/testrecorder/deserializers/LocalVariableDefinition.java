package net.amygdalum.testrecorder.deserializers;

import net.amygdalum.testrecorder.DeserializationException;

public interface LocalVariableDefinition {

	Computation define(LocalVariable local) throws DeserializationException;
	
}
