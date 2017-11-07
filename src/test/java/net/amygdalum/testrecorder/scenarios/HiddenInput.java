package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Recorded;

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