package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class PrimitiveDataTypes {

	public PrimitiveDataTypes() {
	}

	@Recorded
	public boolean booleans(boolean b) {
		return !b;
	}

	@Recorded
	public char chars(char c) {
		return (char) (c + 64);
	}

	@Recorded
	public byte bytes(byte b) {
		return (byte) (b ^ 0b01010101);
	}

	@Recorded
	public short shorts(short s) {
		return (short) (Short.MAX_VALUE - s);
	}

	@Recorded
	public int integers(int i) {
		return i + 1;
	}

	@Recorded
	public float floats(float f) {
		return Math.nextDown(f);
	}

	@Recorded
	public long longs(long l) {
		return -l;
	}

	@Recorded
	public double doubles(double d) {
		return Math.log(d);
	}

}