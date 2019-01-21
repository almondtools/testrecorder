package net.amygdalum.testrecorder.ioscenarios;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;

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
	public byte[] fill(byte[] buffer, byte b) {
		Array.setByte(buffer, 0, b);
		return buffer;
	}

	@Recorded
	public byte extract(byte[] buffer) {
		return Array.getByte(buffer, 0);
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
	public byte[] readFile(byte[] bytes) throws IOException {
		File file = File.createTempFile("StandardLibInputOutput", "tmp");
		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(bytes);
		}
		byte[] readBytes = new byte[bytes.length];
		try (FileInputStream in = new FileInputStream(file)) {
			in.read(readBytes);
		}
		return readBytes;
	}

	@Recorded
	public byte[] readRandomAccessFile(byte[] bytes) throws IOException {
		File file = File.createTempFile("StandardLibInputOutput", "tmp");
		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(bytes);
		}
		byte[] readBytes = new byte[bytes.length];
		try (RandomAccessFile in = new RandomAccessFile(file, "r")) {
			in.readFully(readBytes);
		}
		return readBytes;
	}

	@Recorded
	public float[] serializeRoundtrip(float[] array) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (ObjectOutputStream o = new ObjectOutputStream(out)) {
			o.writeObject(array);
		}
		try (ObjectInputStream i = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()))) {
			Object readObject = i.readObject();
			return (float[]) readObject;
		}
	}

	@Recorded
	public void store(String value) throws IOException {
		new ByteArrayOutputStream().write(value.getBytes());
	}

	@Recorded
	public void storeBuffered(String value) throws IOException {
		File file = File.createTempFile("StandardLibInputOutput", "tmp");
		ByteBuffer buffer = ByteBuffer.wrap(value.getBytes());
		try (RandomAccessFile out = new RandomAccessFile(file, "rw")) {
			out.getChannel().write(new ByteBuffer[] {buffer});
		}
	}

	@Recorded
	public void sleep() throws InterruptedException {
		Thread.sleep(1);
	}

	@Recorded
	public void write(String string) throws IOException {
		File file = File.createTempFile("StandardLibInputOutput", "tmp");
		try (OutputStream o = new FileOutputStream(file)) {
			o.write(string.getBytes());
		}
	}

	@Recorded
	public byte seek(byte[] bytes, int i) throws IOException {
		File file = File.createTempFile("StandardLibInputOutput", "tmp");
		try (FileOutputStream out = new FileOutputStream(file)) {
			out.write(bytes);
		}
		try (RandomAccessFile in = new RandomAccessFile(file, "r")) {
			in.seek(i);
			return in.readByte();
		}
	}

}