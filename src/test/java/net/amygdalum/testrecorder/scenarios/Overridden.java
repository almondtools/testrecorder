package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Recorded;

public class Overridden {

	@Recorded
	public int methodForReplacement(long l) {
		return (int) l;
	}

	@Recorded
	public int methodForExtension(long l) {
		return (int) l;
	}

}
