package net.amygdalum.testrecorder.ioscenarios;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.List;

import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.profile.Methods;

public class StandardLibOutputTestRecorderAgentConfig extends DefaultSerializationProfile {

	@Override
	public List<Methods> getInputs() {
		return emptyList();
	}

	@Override
	public List<Methods> getOutputs() {
		return asList(
			Methods.byDescription("java/lang/reflect/Array", "getByte", "(Ljava/lang/Object;I)B"),
			Methods.byDescription("java/io/OutputStream", "write", "([B)V"),
			Methods.byDescription("java/nio/channels/FileChannel", "write", "([Ljava/nio/ByteBuffer;)J"),
			Methods.byDescription("java/lang/Thread", "sleep", "(J)V"));
	}
}