package net.amygdalum.testrecorder.ioscenarios;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.List;

import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.profile.Methods;

public class StandardLibInputTestRecorderAgentConfig extends DefaultSerializationProfile {

	@Override
	public List<Methods> getInputs() {
		return asList(
			Methods.byDescription("java/lang/System", "currentTimeMillis", "()J"),
			Methods.byDescription("java/io/FileInputStream", "skip", "(J)J"),
			Methods.byDescription("java/io/FileInputStream", "read", "()I"),
			Methods.byDescription("java/io/FileInputStream", "read", "([B)I"),
			Methods.byDescription("java/io/RandomAccessFile", "readBytes", "([BII)I"));
	}

	@Override
	public List<Methods> getOutputs() {
		return emptyList();
	}
}