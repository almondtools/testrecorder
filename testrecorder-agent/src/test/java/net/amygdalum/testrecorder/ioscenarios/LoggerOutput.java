package net.amygdalum.testrecorder.ioscenarios;

import java.util.logging.Logger;

import net.amygdalum.testrecorder.profile.Recorded;

public class LoggerOutput {

	private static final Logger log = Logger.getLogger("output");

	public LoggerOutput() {
	}

	@Recorded
	public boolean isLogging() {
		log.info("something out");
		return true;
	}

}