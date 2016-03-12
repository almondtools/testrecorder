package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Iterator;

import net.amygdalum.testrecorder.Snapshot;
import net.amygdalum.testrecorder.SnapshotExcluded;
import net.amygdalum.testrecorder.SnapshotInput;

public class Inputs {

	@SnapshotExcluded
	private Iterator<String> inputs;

	public Inputs() {
		this.inputs = new ArrayList<>(asList("Hello", " ", "World")).iterator();
	}

	@Snapshot
	public String recorded() {
		String first = read();
		String second = read();
		String third = read();
		return first + second + third;
	}

	public String notrecorded() {
		String first = read();
		String second = read();
		String third = read();
		int i = readInt();
		return first + second + third + i;
	}

	@Snapshot
	public String primitivesRecorded() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("boolean:").append(readBoolean());
		buffer.append("byte:").append(readByte());
		buffer.append("short:").append(readShort());
		buffer.append("int:").append(readInt());
		buffer.append("long:").append(readLong());
		buffer.append("float:").append(readFloat());
		buffer.append("double:").append(readDouble());
		buffer.append("char:").append(readChar());

		return buffer.toString();
	}

	@Snapshot
	public String sideEffectsRecorded() {
		char[] cs = new char[11];
		read(cs);
		return new String(cs);
	}

	@SnapshotInput
	public void read(char[] cs) {
		System.arraycopy("Hello World".toCharArray(), 0, cs, 0, cs.length);
	}

	@SnapshotInput
	public String read() {
		return inputs.next();
	}

	@SnapshotInput
	public byte readByte() {
		return 42;
	}

	@SnapshotInput
	public short readShort() {
		return 42;
	}

	@SnapshotInput
	public int readInt() {
		return 42;
	}

	@SnapshotInput
	public long readLong() {
		return 42;
	}

	@SnapshotInput
	public float readFloat() {
		return 42;
	}

	@SnapshotInput
	public double readDouble() {
		return 42;
	}

	@SnapshotInput
	public boolean readBoolean() {
		return true;
	}

	@SnapshotInput
	public char readChar() {
		return 'a';
	}

}