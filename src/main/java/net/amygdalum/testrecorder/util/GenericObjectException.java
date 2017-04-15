package net.amygdalum.testrecorder.util;

public class GenericObjectException extends RuntimeException {

    public GenericObjectException() {
    }

    public GenericObjectException(String msg, Throwable[] suppressed) {
        super(msg);
        for (Throwable exception : suppressed) {
            addSuppressed(exception);
        }
    }

	public GenericObjectException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
