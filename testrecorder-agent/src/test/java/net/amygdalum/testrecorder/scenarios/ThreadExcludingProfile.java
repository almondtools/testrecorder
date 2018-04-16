package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.profile.Classes;

public class ThreadExcludingProfile extends DefaultSerializationProfile {

	@Override
	public List<Classes> getClasses() {
		return asList(Classes.byPackage("net.amygdalum.testrecorder.scenarios"));
	}

	@Override
	public List<Classes> getClassExclusions() {
		return asList(Classes.byDescription(Thread.class));
	}
}