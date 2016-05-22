package net.amygdalum.testrecorder;

import java.util.IdentityHashMap;
import java.util.Map;

public class ConfigRegistry {

	private static final ConfigRegistry INSTANCE = new ConfigRegistry();

	private static final DefaultTestRecorderAgentConfig DEFAULT = new DefaultTestRecorderAgentConfig();
	
	private Map<Class<? extends TestRecorderAgentConfig>, TestRecorderAgentConfig> configs;

	public ConfigRegistry() {
		this.configs = new IdentityHashMap<>();
	}

	public static TestRecorderAgentConfig loadConfig(Class<? extends TestRecorderAgentConfig> config) {
		return INSTANCE.fetchConfig(config);
	}

	public TestRecorderAgentConfig fetchConfig(Class<? extends TestRecorderAgentConfig> key) {
		return configs.computeIfAbsent(key, k -> createConfig(k));
	}

	public TestRecorderAgentConfig createConfig(Class<? extends TestRecorderAgentConfig> config) {
		try {
			return new FixedTestRecorderAgentConfig(config.newInstance());
		} catch (RuntimeException | ReflectiveOperationException e) {
			return DEFAULT;
		}
	}
}
