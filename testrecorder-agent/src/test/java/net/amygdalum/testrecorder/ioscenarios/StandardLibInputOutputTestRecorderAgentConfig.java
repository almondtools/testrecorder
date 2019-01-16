package net.amygdalum.testrecorder.ioscenarios;

import static java.util.Arrays.asList;

import java.util.List;

import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.profile.Methods;

public class StandardLibInputOutputTestRecorderAgentConfig extends DefaultSerializationProfile {

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
		return asList(
			Methods.byDescription("java/io/OutputStream", "write", "([B)V"),
			Methods.byDescription("java/nio/channels/FileChannel", "write", "([Ljava/nio/ByteBuffer;)J"),
			Methods.byDescription("java/lang/Thread", "sleep", "(J)V"));
	}
}