package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;
import net.amygdalum.testrecorder.profile.SerializationProfile.Global;

public class Globals {
	
	public static int initialized = 0;
	
	@Global
	public static int global = 0;

	@Recorded
	public static void incGlobal() {
		global++;
	}
	
	@Recorded
	public static int getSum() {
		return global + initialized;
	}

}
