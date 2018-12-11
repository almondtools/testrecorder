package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.profile.ProxyClasses.proxies;

import java.util.List;

import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.profile.Classes;

public class ProxyIncludingProfile extends DefaultSerializationProfile {

	@Override
	public List<Classes> getClasses() {
		return asList(proxies());
	}

}