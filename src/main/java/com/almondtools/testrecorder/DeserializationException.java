package com.almondtools.testrecorder;

public class DeserializationException extends RuntimeException {

	public DeserializationException(String value) {
		super("failed deserializing: " + value);
	}

}
