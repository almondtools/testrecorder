package net.amygdalum.testrecorder.ioscenarios;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.amygdalum.testrecorder.profile.Recorded;

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
	public int readFile(byte[] bytes, int ignore) throws IOException {
		File file = File.createTempFile("StandardLibInputOutput", "tmp");
		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(bytes);
		}
		try (FileInputStream in = new FileInputStream(file)) {
			in.skip(ignore);
			return in.read();
		}
	}

	@Recorded
	public void store(String value) throws IOException {
		new ByteArrayOutputStream().write(value.getBytes());
	}

	@Recorded
	public void sleep() throws InterruptedException {
		Thread.sleep(1);
	}

}