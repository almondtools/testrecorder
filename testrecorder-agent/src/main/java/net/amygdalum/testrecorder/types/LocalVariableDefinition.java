package net.amygdalum.testrecorder.types;

public interface LocalVariableDefinition {

	Computation define(LocalVariable local) throws DeserializationException;
	
}
