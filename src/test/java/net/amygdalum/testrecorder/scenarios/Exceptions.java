package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Snapshot;

public class Exceptions {

	public Exceptions() {
	}
	
	@Snapshot
	public void throwingException() {
		throw new IllegalArgumentException();
	}


	
}