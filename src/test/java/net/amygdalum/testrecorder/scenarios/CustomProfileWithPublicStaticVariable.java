package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Recorded;

public class CustomProfileWithPublicStaticVariable {

	public static String istr = "0";
	
	private int i;

	@Recorded(profile=OtherProfile.class)
	public int inc() {
		i++;
		istr = String.valueOf(i);
		return i;
	}
	
}

