package net.amygdalum.testrecorder.ioscenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class HiddenInput {

	private Inputs inputs;

	public HiddenInput() {
		inputs = new Inputs();
	}
	
	@Recorded
	public String inputImmediate() {
		return new Inputs().read();
	}

	@Recorded
	public String inputFromField() {
		return inputs.read();
	}

}