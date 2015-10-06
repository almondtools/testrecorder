package com.almondtools.testrecorder.scenarios;

import com.almondtools.testrecorder.Snapshot;

public class Results {

	public Results() {
	}

	@Snapshot
	public double pow(int i) {
		return Math.pow(i, i);
	}
}