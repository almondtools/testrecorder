package net.amygdalum.testrecorder.runtime;

import net.amygdalum.testrecorder.runtime.TestRecorderAgentInitializer;

public class BrokenAgentInitializer implements TestRecorderAgentInitializer {

	@Override
	public void run() {
		throw new RuntimeException();
	}

}
