package net.amygdalum.testrecorder.runtime;

public class ComparisonException extends Exception {

	private boolean failed;

	public ComparisonException() {
		this(true);
	}

	public ComparisonException(boolean failed) {
		this.failed = failed;
	}

	public boolean failed() {
		return failed;
	}

}
