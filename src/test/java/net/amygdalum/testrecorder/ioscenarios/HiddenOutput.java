package net.amygdalum.testrecorder.ioscenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class HiddenOutput {

	private Outputs outputs;

	public HiddenOutput() {
		outputs = new Outputs();
	}
	
	@Recorded
	public void outputImmediate(String text) {
		new Outputs().print(text);
	}

	@Recorded
	public void outputToField(String text) {
		outputs.print(text);
	}
}