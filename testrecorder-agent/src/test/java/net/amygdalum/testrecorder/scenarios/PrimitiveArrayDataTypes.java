package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class PrimitiveArrayDataTypes {

	public PrimitiveArrayDataTypes() {
	}

	@Recorded
	public boolean booleans(boolean[] b) {
		boolean temp = b[0];
		for (int i = 0; i < b.length - 1; i++) {
			b[i] = b[i+1];
		}
		b[b.length -1 ] = temp;
		return temp;
	}

	@Recorded
	public char chars(char[] c) {
		char temp = c[0];
		for (int i = 0; i < c.length - 1; i++) {
			c[i] = c[i+1];
		}
		c[c.length -1 ] = temp;
		return temp;
	}

	@Recorded
	public byte bytes(byte[] b) {
		byte temp = b[0];
		for (int i = 0; i < b.length - 1; i++) {
			b[i] = b[i+1];
		}
		b[b.length -1 ] = temp;
		return temp;
	}

	@Recorded
	public short shorts(short[] s) {
		short temp = s[0];
		for (int i = 0; i < s.length - 1; i++) {
			s[i] = s[i+1];
		}
		s[s.length -1 ] = temp;
		return temp;
	}

	@Recorded
	public int integers(int[] in) {
		int temp = in[0];
		for (int i = 0; i < in.length - 1; i++) {
			in[i] = in[i+1];
		}
		in[in.length -1 ] = temp;
		return temp;
	}

	@Recorded
	public float floats(float[] f) {
		float temp = f[0];
		for (int i = 0; i < f.length - 1; i++) {
			f[i] = f[i+1];
		}
		f[f.length -1 ] = temp;
		return temp;
	}

	@Recorded
	public long longs(long[] l) {
		long temp = l[0];
		for (int i = 0; i < l.length - 1; i++) {
			l[i] = l[i+1];
		}
		l[l.length -1 ] = temp;
		return temp;
	}

	@Recorded
	public double doubles(double[] d) {
		double temp = d[0];
		for (int i = 0; i < d.length - 1; i++) {
			d[i] = d[i+1];
		}
		d[d.length -1 ] = temp;
		return temp;
	}

}