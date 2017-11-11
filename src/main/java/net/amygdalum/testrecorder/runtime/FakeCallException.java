package net.amygdalum.testrecorder.runtime;

public class FakeCallException extends RuntimeException {

	public FakeCallException(Exception e) {
		super(e);
	}

}
