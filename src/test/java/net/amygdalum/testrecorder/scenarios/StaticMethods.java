package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Snapshot;

public class StaticMethods {

	private String value;

	public StaticMethods(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	@Snapshot
	public static StaticMethods from(String str) {
		return new StaticMethods(str); 
	}
	
}