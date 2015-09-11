package com.almondtools.invivoderived.scenarios;

import com.almondtools.invivoderived.Snapshot;

public class SideEffects {

	private int i;

	public SideEffects() {
	}
	
	public int getI() {
		return i;
	}
	
	@Snapshot
	public void methodWithSideEffectOnThis(int i) {
		this.i = i + 1;
	}
	
	@Snapshot
	public void methodWithSideEffectOnArgument(int[] i) {
		i[0]++;
	}
	
	
}