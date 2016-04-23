package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.lang.reflect.Field;
import java.util.List;

import net.amygdalum.testrecorder.Snapshot;
import net.amygdalum.testrecorder.profile.DefaultSerializationProfile;

public class CustomProfileWithPublicStaticVariable {

	public static String istr = "0";
	
	private int i;

	@Snapshot(profile=OtherProfile.class)
	public int inc() {
		i++;
		istr = String.valueOf(i);
		return i;
	}

	public static class OtherProfile extends DefaultSerializationProfile {
		@Override
		public List<Field> getGlobalFields()  {
			try {
				return asList(CustomProfileWithPublicStaticVariable.class.getDeclaredField("istr"));
			} catch (NoSuchFieldException | SecurityException e) {
				return emptyList();
			}
		}
	}
	
}

