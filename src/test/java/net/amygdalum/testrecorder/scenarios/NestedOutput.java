package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Recorded;
import net.amygdalum.testrecorder.SerializationProfile.Output;

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