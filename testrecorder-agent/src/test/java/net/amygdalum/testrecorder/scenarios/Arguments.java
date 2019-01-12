package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;
import net.amygdalum.testrecorder.util.testobjects.Container;

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
	public String string(String s) {
		return s;
	}

	@Recorded
	public String towordprimitiveAndString(double d, String s) {
		return "" + d + s;
	}

	@Recorded
	public String mixed(String s, long l, int i, double d) {
		return s + l + i + d;
	}

	@Recorded
	public <T> String argumentNoModification(Container<T> object) {
		return object.toString();
	}

	@Recorded
	public <T> String argumentModification(Container<T> object, T content) {
		object.setContent(content);
		return object.toString();
	}
}