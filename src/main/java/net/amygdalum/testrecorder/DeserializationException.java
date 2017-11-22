package net.amygdalum.testrecorder;

public class DeserializationException extends RuntimeException {

	public DeserializationException(Throwable e) {
		super(e);
	}

	public DeserializationException(String message, Throwable e) {
		super(message, e);
	}

	public DeserializationException(String message) {
		super(message);
	}

}
