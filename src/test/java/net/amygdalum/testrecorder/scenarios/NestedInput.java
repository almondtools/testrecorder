package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Recorded;
import net.amygdalum.testrecorder.SerializationProfile.Input;

public class NestedInput {

	private String timestamp;

	public NestedInput() {
	}

	@Recorded
	public String getTime() {
        timestamp = hours() + ":" + minutes();
        return timestamp;
    }

	@Input
	public long millis() {
		return System.currentTimeMillis();
	}
		
	@Input
	public long seconds() {
		return millis() / 1000;
	}
		
	@Input
	public long minutes() {
		return seconds() / 60;
	}
		
	@Input
	public long hours() {
		return minutes() / 60;
	}

}