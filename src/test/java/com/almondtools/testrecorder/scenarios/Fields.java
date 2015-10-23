package com.almondtools.testrecorder.scenarios;

import com.almondtools.testrecorder.Snapshot;

public class Fields {

	@Snapshot
	private byte b;
	@Snapshot
	private short s;
	@Snapshot
	private int i;
	@Snapshot
	private long l;
	@Snapshot
	private char c;
	@Snapshot
	private boolean z;
	@Snapshot
	private float f;
	@Snapshot
	private double d;
	@Snapshot
	private Object o;

	public Fields() {
	}

	public byte getB() {
		return b;
	}

	public short getS() {
		return s;
	}

	public int getI() {
		return i;
	}

	public long getL() {
		return l;
	}

	public char getC() {
		return c;
	}

	public boolean isZ() {
		return z;
	}

	public float getF() {
		return f;
	}

	public double getD() {
		return d;
	}

	public Object getO() {
		return o;
	}

	public void all(long max) {
		for (int it = 0; it < max; it++) {
			b(it);
			s(it);
			i(it);
			l( it);
			c(it);
			z(it);
			f(it);
			d(it);
			o(it);
		}
	}

	public void o(int it) {
		o = "s:" + it;
	}

	public void d(int it) {
		d = Math.pow(it, it);
	}

	public void f(int it) {
		f = (float) Math.pow(it - 1, it - 1);
	}

	public void z(int it) {
		z = it % 2 == 0;
	}

	public void c(int it) {
		c = (char) (it + 'a');
	}

	public void l(int it) {
		l = it * it + 1;
	}

	public void i(int it) {
		i = it * it;
	}

	public void s(int it) {
		s = (short) (it + 1);
	}

	public void b(int it) {
		b = (byte) it;
	}
}