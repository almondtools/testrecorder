package net.amygdalum.testrecorder.util;

import net.amygdalum.testrecorder.runtime.TestRecorderAgentInitializer;

public class AgentInitializer implements TestRecorderAgentInitializer {

	@Override
	public void run() {
		Logger.info("init");
	}

}
