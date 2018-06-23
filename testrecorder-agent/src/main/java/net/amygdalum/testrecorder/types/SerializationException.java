package net.amygdalum.testrecorder.types;

public class SerializationException extends RuntimeException {

	public SerializationException(Throwable e) {
		super(e);
	}

	public SerializationException(String msg) {
		super(msg);
	}

}
