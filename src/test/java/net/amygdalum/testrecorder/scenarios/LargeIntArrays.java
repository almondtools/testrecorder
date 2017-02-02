package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Snapshot;

public class LargeIntArrays {
	
	private int[][] entries;

	public LargeIntArrays(int entries) {
		this.entries = initInts(entries);
	}

	public LargeIntArrays() {
		this.entries = new int[0][0];
	}

	@Snapshot
	public int[][] initInts(int entries) {
		int counter = 0;
		int[][] is = new int[entries][entries];
		for (int i = 0; i < is.length; i++) {
			for (int j = 0; j < is[i].length; j++) {
				is[i][j] = counter++;
			}
		}
		return is;
	}

	@Snapshot
	public long sum() {
		long sum = 0;
		for (int i = 0; i < entries.length; i++) {
			for (int j = 0; j < entries[i].length; j++) {
				sum += entries[i][j];
			}
		}
		return sum;
	}


}
