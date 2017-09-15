package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;
import net.amygdalum.testrecorder.Packages;

public class ScenarioAgentConfig extends DefaultTestRecorderAgentConfig {

	@Override
	public List<Packages> getPackages() {
		return asList(Packages.byName("net.amygdalum.testrecorder.scenarios"));
	}
}