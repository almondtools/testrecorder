package net.amygdalum.testrecorder.ioscenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class ImplementedInput implements InterfacedInput {

	@Recorded
	public String recorded() {
		return String.valueOf(input());
	}

	@Override
	public long input() {
		return System.currentTimeMillis();
	}
	
}