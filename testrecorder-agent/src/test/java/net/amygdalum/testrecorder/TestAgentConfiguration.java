package net.amygdalum.testrecorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.ClassPathConfigurationLoader;
import net.amygdalum.testrecorder.profile.DefaultConfigurationLoader;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.profile.SerializationProfile;

public class TestAgentConfiguration extends AgentConfiguration {

	private Map<Class<?>, List<Function<Object[], ?>>> configs;
	
	public TestAgentConfiguration() {
		super(new ClassPathConfigurationLoader(), new DefaultConfigurationLoader());
		configs = new HashMap<>();
	}

	public TestAgentConfiguration(ClassLoader loader) {
		super(new ClassPathConfigurationLoader(loader), new DefaultConfigurationLoader(loader));
		configs = new HashMap<>();
	}

	public TestAgentConfiguration reset() {
		return (TestAgentConfiguration) super.reset();
	}

	public TestAgentConfiguration withLoader(ClassLoader loader) {
		setConfigurationLoaders(new ClassPathConfigurationLoader(loader), new DefaultConfigurationLoader(loader));
		return this;
	}
	
	public static TestAgentConfiguration defaultConfig() {
		return (TestAgentConfiguration) new TestAgentConfiguration()
			.withDefaultValue(SerializationProfile.class, DefaultSerializationProfile::new)
			.withDefaultValue(PerformanceProfile.class, DefaultPerformanceProfile::new)
			.withDefaultValue(SnapshotConsumer.class, DefaultSnapshotConsumer::new);
	}

	public <T> TestAgentConfiguration loading(Class<T> clazz, Function<Object[], T> supplier) {
		configs.computeIfAbsent(clazz, key -> new ArrayList<>()).add(supplier);
		return this;
	}
	
	@Override
	protected <T> Stream<T> load(Class<T> clazz, Object... args) {
		List<Function<Object[], ?>> suppliers = configs.get(clazz);
		if (suppliers != null) {
			return suppliers.stream()
				.map(supplier -> supplier.apply(args))
				.map(object -> clazz.cast(object));
		}
		return super.load(clazz, args);
	}

}
