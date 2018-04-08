package net.amygdalum.testrecorder.scenarios;

public class Overriding extends Overridden {
	@Override
	public int methodForExtension(long l) {
		return super.methodForExtension(l) + 1;
	}

	@Override
	public int methodForReplacement(long l) {
		return (int) l + 1;
	}
}
