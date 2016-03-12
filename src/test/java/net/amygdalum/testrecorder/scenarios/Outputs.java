package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Snapshot;
import net.amygdalum.testrecorder.SnapshotOutput;

public class Outputs {

	public Outputs() {
	}
	
	@Snapshot
	public void recorded() {
		print("Hello ");
		print("World");
	}

	public void notrecorded() {
		print("Hello ");
		print("World");
	}
	
	@Snapshot
	public void primitivesRecorded() {
		printByte((byte) 1);
		printShort((short) 2);
		printInt(3);
		printLong(4l);
		printFloat(-1.2f);
		printDouble(1.1d);
		printBoolean(true);
		printChar('b');
	}

	@SnapshotOutput
	public void print(String s) {
	}

	@SnapshotOutput
	public void printByte(byte value) {
	}

	@SnapshotOutput
	public void printShort(short value) {
	}

	@SnapshotOutput
	public void printInt(int value) {
	}
	
	@SnapshotOutput
	public void printLong(long value) {
	}
	
	@SnapshotOutput
	public void printFloat(float value) {
	}
	
	@SnapshotOutput
	public void printDouble(double value) {
	}
	
	@SnapshotOutput
	public void printBoolean(boolean value) {
	}
	
	@SnapshotOutput
	public void printChar(char value) {
	}
	
}