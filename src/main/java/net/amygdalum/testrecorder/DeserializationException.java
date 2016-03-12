package net.amygdalum.testrecorder;

public class DeserializationException extends RuntimeException {

	public DeserializationException(String value) {
		super("failed deserializing: " + value);
	}

}
