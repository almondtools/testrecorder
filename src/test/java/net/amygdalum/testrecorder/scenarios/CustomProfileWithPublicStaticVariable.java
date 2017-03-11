package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Recorded;
import net.amygdalum.testrecorder.SerializationProfile.Global;

public class CustomProfileWithPublicStaticVariable {

	public static String istr = "0";
	
	@Global
	public static int iAnnotated = 0;
	
	private int i;

	@Recorded(profile=OtherProfile.class)
	public int inc() {
		i++;
		istr = String.valueOf(i);
		iAnnotated = i;
		return i;
	}
	
}

