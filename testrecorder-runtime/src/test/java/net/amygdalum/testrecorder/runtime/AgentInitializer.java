package net.amygdalum.testrecorder.runtime;

import net.amygdalum.testrecorder.runtime.TestRecorderAgentInitializer;
import net.amygdalum.testrecorder.util.Logger;

public class AgentInitializer implements TestRecorderAgentInitializer {

	@Override
	public void run() {
		Logger.info("init");
	}

}
