package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.profile.Recorded;
import net.amygdalum.testrecorder.profile.SerializationProfile.Global;
import net.amygdalum.testrecorder.profile.SerializationProfile.Input;
import net.amygdalum.testrecorder.profile.SerializationProfile.Output;

public class Annotations {
	
	@Global
	private static String global;
	@Global
	private String notGlobal;
	
	@Output
	public void output() {
	}

	@Output
	@Recorded
	public void outputRecorded() {
	}

	@Input
	public void input() {
	}

	@Input
	@Recorded
	public void inputRecorded() {
	}

	@Input
	@Output
	public void bothInputAndOutput() {
	}
}