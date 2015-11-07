package com.almondtools.testrecorder;

import java.util.IdentityHashMap;
import java.util.Map;

public class ConfigRegistry {

	private static final ConfigRegistry INSTANCE = new ConfigRegistry();

	private static final DefaultConfig DEFAULT = new DefaultConfig();
	
	private Map<Class<? extends SnapshotConfig>, SnapshotConfig> configs;

	public ConfigRegistry() {
		this.configs = new IdentityHashMap<>();
	}

	public static SnapshotConfig loadConfig(Class<? extends SnapshotConfig> config) {
		return INSTANCE.fetchConfig(config);
	}

	public SnapshotConfig fetchConfig(Class<? extends SnapshotConfig> key) {
		return configs.computeIfAbsent(key, k -> createConfig(k));
	}

	public SnapshotConfig createConfig(Class<? extends SnapshotConfig> config) {
		try {
			return config.newInstance();
		} catch (RuntimeException | ReflectiveOperationException e) {
			return DEFAULT;
		}
	}
}
