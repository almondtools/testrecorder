package net.amygdalum.testrecorder.scenarios;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.amygdalum.testrecorder.Recorded;

public class StandardLibInputOutput {

	private long timestamp;

	public StandardLibInputOutput() {
	}

	@Recorded
	public long getTimestamp() {
        timestamp = System.currentTimeMillis();
        return timestamp;
    }

    @Recorded
	public void store(String value) throws IOException {
        new ByteArrayOutputStream().write(value.getBytes());
    }

}