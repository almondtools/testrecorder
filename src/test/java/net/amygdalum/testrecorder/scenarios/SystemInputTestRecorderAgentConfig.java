package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;
import net.amygdalum.testrecorder.Methods;

public class SystemInputTestRecorderAgentConfig extends DefaultTestRecorderAgentConfig {

	@Override
	public List<Methods> getInputs() {
		return asList(Methods.byDescription("net/amygdalum/testrecorder/scenarios/SystemInput", "currentTimeMillis", "()J"));
	}
}