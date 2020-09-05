package net.amygdalum.testrecorder.ioscenarios;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.List;

import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.profile.Methods;

public class LoggerOutputTestRecorderAgentConfig extends DefaultSerializationProfile {

	@Override
	public List<Methods> getInputs() {
		return emptyList();
	}

	@Override
	public List<Methods> getOutputs() {
		return asList(
			Methods.byDescription("java/util/logging/Logger", "info", "(Ljava/lang/String;)V"));
	}
}