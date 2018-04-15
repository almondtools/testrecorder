package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.profile.Global;
import net.amygdalum.testrecorder.profile.Input;
import net.amygdalum.testrecorder.profile.Output;
import net.amygdalum.testrecorder.profile.Recorded;

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