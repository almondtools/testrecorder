package net.amygdalum.testrecorder.scenarios;

public class State {
	private String s;
	
	public State() {
		this.s = "";
	}
	
	public String next() {
		String current = s;
		s += '.';
		return current;
	}

}