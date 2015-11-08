package com.almondtools.testrecorder.scenarios;

import com.almondtools.testrecorder.Snapshot;

public class ConstructorBean {

	private int i;
	private Object o;
	
	public ConstructorBean(int i, Object o) {
		this.i = i;
		this.o = o;
	}
	
	@Snapshot
	@Override
	public int hashCode() {
		int j = o == null ? 1 : o.hashCode();
		return i + 13 * j;
	}

}