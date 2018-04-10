package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class AlmostBean {

	private String string;
	private String other;
	
	public AlmostBean() {
	}

	public void setSTRING(String string) {
		this.string = string;
	}
	
	public String getSTRING() {
		return string;
	}

	public void setOther(String other) {
		this.other = other;
	}
	
	public String getOther() {
		return other;
	}

	@Recorded
	@Override
	public int hashCode() {
		return string.length();
	}

}