package net.amygdalum.testrecorder.util;

import net.amygdalum.testrecorder.TestRecorderAgentInitializer;

public class BrokenAgentInitializer implements TestRecorderAgentInitializer {

	@Override
	public void run() {
		throw new RuntimeException();
	}

}
