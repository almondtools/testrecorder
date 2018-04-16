package net.amygdalum.testrecorder.ioscenarios;

import net.amygdalum.testrecorder.profile.Output;
import net.amygdalum.testrecorder.profile.Recorded;

public class NestedOutput {

	private int timestamp;

	public NestedOutput() {
		timestamp = 0;
	}

	@Recorded
	public int getTime() {
        timestamp++;
        printInt(timestamp);
        return timestamp;
    }

	@Output
	public void print(String s) {
	}

	@Output
	public void printInt(int value) {
		print(String.valueOf(value));
	}
	
}