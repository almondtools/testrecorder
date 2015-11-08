package com.almondtools.testrecorder.scenarios;

import com.almondtools.testrecorder.Snapshot;

public class SubBean extends SuperBean {

	private Object o;
	
	public SubBean() {
	}
	
	public void setO(Object o) {
		this.o = o;
	}
	
	@Snapshot
	@Override
	public int hashCode() {
		int j = o == null ? 1 : o.hashCode();
		return getI() + 13 * j;
	}

}