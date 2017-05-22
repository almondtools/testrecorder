package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Recorded;

public class SystemInput {

	private long timestamp;

	public SystemInput() {
	}

	@Recorded
	public long getTimestamp() {
        timestamp = currentTimeMillis();
        return timestamp;
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

}