package com.almondtools.testrecorder.scenarios;

import com.almondtools.testrecorder.Snapshot;

public class Bean {

	private int i;
	private Object o;
	
	public Bean() {
	}
	
	public void setI(int i) {
		this.i = i;
	}
	
	public void setO(Object o) {
		this.o = o;
	}
	
	@Snapshot
	@Override
	public int hashCode() {
		int j = o == null ? 1 : o.hashCode();
		return i + 13 * j;
	}

}