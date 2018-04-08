package net.amygdalum.testrecorder.profile;

public class BrokenConfigNoArguments implements ConfigNoArguments {
	
	public BrokenConfigNoArguments() {
		throw new RuntimeException();
	}

}
