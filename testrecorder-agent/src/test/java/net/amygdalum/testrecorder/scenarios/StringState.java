package net.amygdalum.testrecorder.scenarios;

public class StringState implements State {
	private String s;
	
	public StringState() {
		this.s = "";
	}
	
	private StringState(String s) {
		this.s = s;
	}
	
	public static StringState create(String s) {
		return new StringState(s);
	}
	
	@Override
	public String next() {
		String current = s;
		s += '.';
		return current;
	}

}