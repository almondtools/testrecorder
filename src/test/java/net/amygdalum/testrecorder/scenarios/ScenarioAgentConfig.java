package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;

public class ScenarioAgentConfig extends DefaultTestRecorderAgentConfig {

	@Override
	public List<String> getPackages() {
		return asList("net.amygdalum.testrecorder.scenarios");
	}
}