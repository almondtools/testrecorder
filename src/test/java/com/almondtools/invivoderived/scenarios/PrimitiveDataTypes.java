package com.almondtools.invivoderived.scenarios;

import com.almondtools.invivoderived.analyzer.Snapshot;

public class PrimitiveDataTypes {

	public PrimitiveDataTypes() {
	}

	@Snapshot
	public boolean booleans(boolean b) {
		return !b;
	}

	@Snapshot
	public char chars(char c) {
		return (char) (c + 64);
	}

	@Snapshot
	public byte bytes(byte b) {
		return (byte) (b ^ 0b01010101);
	}

	@Snapshot
	public short shorts(short s) {
		return (short) (Short.MAX_VALUE - s);
	}

	@Snapshot
	public int integers(int i) {
		return i + 1;
	}

	@Snapshot
	public float floats(float f) {
		return Math.nextDown(f);
	}

	@Snapshot
	public long longs(long l) {
		return -l;
	}

	@Snapshot
	public double doubles(double d) {
		return Math.log(d);
	}

}