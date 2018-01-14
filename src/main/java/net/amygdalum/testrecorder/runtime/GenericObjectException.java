package net.amygdalum.testrecorder.runtime;

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

	public GenericObjectException(String msg) {
		super(msg);
	}

}
