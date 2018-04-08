package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.DefaultPerformanceProfile;
import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.DefaultSnapshotConsumer;
import net.amygdalum.testrecorder.SnapshotConsumer;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.profile.SerializationProfile;

public class TestAgentConfiguration extends AgentConfiguration {

	public TestAgentConfiguration(String... agentargs) {
		super(agentargs);
	}

	public TestAgentConfiguration(ClassLoader loader, String... agentargs) {
		super(loader, agentargs);
	}

	public TestAgentConfiguration reset() {
		return (TestAgentConfiguration) super.reset();
	}

	public TestAgentConfiguration withLoader(ClassLoader loader) {
		setLoader(loader);
		return this;
	}

	public static TestAgentConfiguration defaultConfig() {
		return (TestAgentConfiguration) new TestAgentConfiguration()
			.withDefaultValue(SerializationProfile.class, DefaultSerializationProfile::new)
			.withDefaultValue(PerformanceProfile.class, DefaultPerformanceProfile::new)
			.withDefaultValue(SnapshotConsumer.class, DefaultSnapshotConsumer::new);
	}

}
