package com.almondtools.testrecorder.scenarios;

import com.almondtools.testrecorder.Snapshot;

public class AmbiguousConstructorBean {

	private int i;
	private int j;
	private Object o;
	
	public AmbiguousConstructorBean(int i, int j, Object o) {
		this.i = i;
		this.j = j;
		this.o = o;
	}
	
	public int getJ() {
		return j;
	}
	
	@Snapshot
	@Override
	public int hashCode() {
		int j = o == null ? 1 : o.hashCode();
		return i + 13 * j;
	}

}