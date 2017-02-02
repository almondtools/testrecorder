package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Snapshot;

public class LargeObjectArrays {

	private AnObject[][] entries;

	public LargeObjectArrays(int entries) {
		this.entries = initObjects(entries);
	}

	public LargeObjectArrays() {
		this.entries = new AnObject[0][0];
	}

	@Snapshot
	public AnObject[][] initObjects(int entries) {
		int counter = 0;
		AnObject[][] is = new AnObject[entries][entries];
		for (int i = 0; i < is.length; i++) {
			for (int j = 0; j < is[i].length; j++) {
				is[i][j] = new AnObject(counter);
				counter++;
			}
		}
		return is;
	}

	@Snapshot
	public long sum() {
		long sum = 0;
		for (int i = 0; i < entries.length; i++) {
			for (int j = 0; j < entries[i].length; j++) {
				sum += entries[i][j].i;
			}
		}
		return sum;
	}

	public static class AnObject {
		public int i;

		public AnObject(int i) {
			this.i = i;
		}
		
	}
}
