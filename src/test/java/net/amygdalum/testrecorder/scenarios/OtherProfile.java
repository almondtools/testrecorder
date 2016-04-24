package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.lang.reflect.Field;
import java.util.List;

import net.amygdalum.testrecorder.profile.DefaultSerializationProfile;

public class OtherProfile extends DefaultSerializationProfile {
	@Override
	public List<Field> getGlobalFields()  {
		try {
			return asList(CustomProfileWithPublicStaticVariable.class.getDeclaredField("istr"));
		} catch (NoSuchFieldException | SecurityException e) {
			return emptyList();
		}
	}
}