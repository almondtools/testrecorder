package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class SideEffects {

	private int i;

	public SideEffects() {
	}
	
	public int getI() {
		return i;
	}
	
	@Recorded
	public void methodWithSideEffectOnThis(int i) {
		this.i = i + 1;
	}
	
	@Recorded
	public void methodWithSideEffectOnArgument(int[] i) {
		i[0]++;
	}
	
	
}