package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.hints.Setter;
import net.amygdalum.testrecorder.profile.Recorded;

public class CustomConstructed {

	private String string;
	private String other;

	public CustomConstructed() {
	}

	@Setter
	public void string(String string) {
		this.string = string;
	}

	public void other(String other) {
		this.other = other;
	}

	public String getString() {
		return string;
	}

	public String getOther() {
		return other;
	}

	@Recorded
	@Override
	public int hashCode() {
		return (string == null ? 0 : string.length())
			+ (other == null ? 0 : other.length());
	}

}