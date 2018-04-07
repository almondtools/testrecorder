package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class Arguments {

	public Arguments() {
	}

	@Recorded
	public String primitive(int i) {
		return "" + i;
	}

	@Recorded
	public String towordprimitive(long l) {
		return "" + l;
	}

	@Recorded
	public String object(String s) {
		return s;
	}

	@Recorded
	public String towordprimitiveAndObject(double d, String s) {
		return "" + d + s;
	}

	@Recorded
	public String mixed(String s, long l, int i, double d) {
		return s + l + i + d;
	}
}