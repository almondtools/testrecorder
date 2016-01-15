package com.almondtools.testrecorder.scenarios;

import com.almondtools.testrecorder.Snapshot;

public class HiddenInnerClass {

	private Hidden o;
	
	public HiddenInnerClass(String name) {
		this.o = new Hidden(name);
	}
	
	@Snapshot
	@Override
	public String toString() {
		return o.getName();
	}

	private static class Hidden {
		private String name;
		
		public Hidden(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
}