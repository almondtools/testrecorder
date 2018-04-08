package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class StaticMethods {

	private String value;

	public StaticMethods(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	@Recorded
	public static StaticMethods from(String str) {
		return new StaticMethods(str); 
	}
	
}