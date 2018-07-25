package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Global;
import net.amygdalum.testrecorder.profile.Recorded;

public class StaticMethodAndState {
	@Global
    public static String global;

	private StaticMethodAndState() {
	}

	@Recorded
	public static void setGlobal(String global) {
		StaticMethodAndState.global = global;
	}
	
	public static String getGlobal() {
		return global;
	}
	
}