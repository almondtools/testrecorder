package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Snapshot;

public class Results {

	public Results() {
	}

	@Snapshot
	public double pow(int i) {
		return Math.pow(i, i);
	}
}