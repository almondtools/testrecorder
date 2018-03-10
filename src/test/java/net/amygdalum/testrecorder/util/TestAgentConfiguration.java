package net.amygdalum.testrecorder.util;

import java.util.Optional;

import net.amygdalum.testrecorder.AgentConfiguration;
import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;
import net.amygdalum.testrecorder.TestRecorderAgentConfig;

public class TestAgentConfiguration extends AgentConfiguration {

	private TestRecorderAgentConfig config;

	public TestAgentConfiguration() {
		this(new DefaultTestRecorderAgentConfig());
	}

	public TestAgentConfiguration(TestRecorderAgentConfig config) {
		super(TestAgentConfiguration.class.getClassLoader());
		this.config = config;
	}
	
	@Override
	public <T> Optional<T> loadConfiguration(Class<T> clazz, Object... args) {
		if (clazz == TestRecorderAgentConfig.class) {
			return Optional.of(clazz.cast(config));
		}
		return super.loadConfiguration(clazz, args);
	}

}
