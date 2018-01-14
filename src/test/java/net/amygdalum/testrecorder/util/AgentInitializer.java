package net.amygdalum.testrecorder.util;

import net.amygdalum.testrecorder.Logger;
import net.amygdalum.testrecorder.TestRecorderAgentInitializer;

public class AgentInitializer implements TestRecorderAgentInitializer {

	@Override
	public void run() {
		Logger.info("init");
	}

}
