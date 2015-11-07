package com.almondtools.testrecorder;

import java.util.IdentityHashMap;
import java.util.List;
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
			return new ImmutableSnapshotConfig(config.newInstance());
		} catch (RuntimeException | ReflectiveOperationException e) {
			return DEFAULT;
		}
	}

	private static class ImmutableSnapshotConfig implements SnapshotConfig {

		private MethodSnapshotConsumer methodConsumer;
		private ValueSnapshotConsumer valueConsumer;
		private long timeoutInMillis;
		private List<String> packages;

		public ImmutableSnapshotConfig(SnapshotConfig config) {
			this.methodConsumer = config.getMethodConsumer();
			this.valueConsumer = config.getValueConsumer();
			this.timeoutInMillis = config.getTimeoutInMillis();
			this.packages = config.getPackages();
		}

		@Override
		public MethodSnapshotConsumer getMethodConsumer() {
			return methodConsumer;
		}

		@Override
		public ValueSnapshotConsumer getValueConsumer() {
			return valueConsumer;
		}

		@Override
		public long getTimeoutInMillis() {
			return timeoutInMillis;
		}

		@Override
		public List<String> getPackages() {
			return packages;
		}

	}
}
