package net.amygdalum.testrecorder.scenarios;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;
import net.amygdalum.testrecorder.Methods;

public class StandardLibInputOutputTestRecorderAgentConfig extends DefaultTestRecorderAgentConfig {

	@Override
	public List<Methods> getInputs() {
		return asList(Methods.byDescription("java/lang/System", "currentTimeMillis", "()J"));
	}
	
	@Override
	public List<Methods> getOutputs() {
		return asList(Methods.byDescription("java/io/ByteOutputStream", "write", "([b)V"));
	}
}