package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class Exceptions {

	public Exceptions() {
	}
	
	@Recorded
	public void throwingException() {
		throw new IllegalArgumentException();
	}


	
}