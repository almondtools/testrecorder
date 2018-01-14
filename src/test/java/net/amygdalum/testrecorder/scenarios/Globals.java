package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Recorded;
import net.amygdalum.testrecorder.profile.SerializationProfile.Global;

public class Globals {
	
	@Global
	public static int global = 0;

	@Recorded
	public static void incGlobal() {
		global++;
	}

}
