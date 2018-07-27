package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Global;
import net.amygdalum.testrecorder.profile.Recorded;

public class GlobalFields {
	@Global
    public static String global;

	private GlobalFields() {
	}

	@Recorded
	public static void setGlobal(String global) {
		GlobalFields.global = global;
	}
	
	public static String getGlobal() {
		return global;
	}

}