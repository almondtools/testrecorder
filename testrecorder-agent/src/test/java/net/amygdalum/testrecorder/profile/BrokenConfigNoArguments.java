package net.amygdalum.testrecorder.profile;

public class BrokenConfigNoArguments implements ConfigNoArgumentsNonExclusive {
	
	public BrokenConfigNoArguments() {
		throw new RuntimeException();
	}

}
