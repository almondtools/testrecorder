package com.almondtools.testrecorder.scenarios;

import com.almondtools.testrecorder.Snapshot;
import com.almondtools.testrecorder.SnapshotOutput;

public class Outputs {

	public Outputs() {
	}
	
	@Snapshot
	public void recorded() {
		print("Hello ");
		print("World");
	}

	public void notrecorded() {
		print("Hello ");
		print("World");
	}
	
	@SnapshotOutput
	public void print(String s) {
		System.out.println("out:" + s);
	}
}