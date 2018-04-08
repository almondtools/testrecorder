package net.amygdalum.testrecorder.util.testobjects;

public class Complex {

	private Simple simple;

	public Complex() {
		this.simple = new Simple("otherStr");
	}

	public Complex(String simpleStr) {
		this.simple = new Simple(simpleStr);
	}

	public Simple getSimple() {
		return simple;
	}
	
	@Override
	public String toString() {
	    return getClass().getSimpleName();
	}
}