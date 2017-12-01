package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;
import net.amygdalum.testrecorder.profile.Classes;

public class ScenarioAgentConfig extends DefaultTestRecorderAgentConfig {

	@Override
	public List<Classes> getClasses() {
		return asList(Classes.byPackage("net.amygdalum.testrecorder.scenarios"));
	}
}