package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class Results {

	public Results() {
	}

	@Recorded
	public double pow(int i) {
		return Math.pow(i, i);
	}
}