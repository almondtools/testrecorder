package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Snapshot;

public class CustomProfileWithPublicStaticVariable {

	public static String istr = "0";
	
	private int i;

	@Snapshot(profile=OtherProfile.class)
	public int inc() {
		i++;
		istr = String.valueOf(i);
		return i;
	}
	
}

