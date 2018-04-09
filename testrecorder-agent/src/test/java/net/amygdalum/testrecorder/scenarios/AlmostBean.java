package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class AlmostBean {

	private String string;
	
	public AlmostBean() {
	}

	public void setSTRING(String string) {
		this.string = string;
	}
	
	public String getSTRING() {
		return string;
	}

	@Recorded
	@Override
	public int hashCode() {
		return string.length();
	}

}