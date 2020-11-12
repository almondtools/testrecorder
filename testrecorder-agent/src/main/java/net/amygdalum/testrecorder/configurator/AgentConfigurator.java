package net.amygdalum.testrecorder.configurator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.amygdalum.testrecorder.DefaultPerformanceProfile;
import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.DefaultSnapshotConsumer;
import net.amygdalum.testrecorder.deserializers.builder.DefaultSetupGenerators;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerator;
import net.amygdalum.testrecorder.deserializers.matcher.DefaultMatcherGenerators;
import net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerator;
import net.amygdalum.testrecorder.extensionpoint.ExtensionPoint;
import net.amygdalum.testrecorder.generator.DefaultTestGeneratorProfile;
import net.amygdalum.testrecorder.generator.TestGeneratorProfile;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.ConfigurationLoader;
import net.amygdalum.testrecorder.profile.FixedConfigurationLoader;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.profile.SerializationProfile;
import net.amygdalum.testrecorder.profile.SnapshotConsumer;
import net.amygdalum.testrecorder.serializers.DefaultSerializers;
import net.amygdalum.testrecorder.types.Serializer;

public class AgentConfigurator {

	private Map<Class<?>, FixedConfigurationLoader> configurationLoaders;
	private List<ConfigurationLoader> fallbackLoaders;

	public AgentConfigurator() {
		this.configurationLoaders = new LinkedHashMap<>();
		this.fallbackLoaders = new ArrayList<>();
	}

	public AgentConfigurator generateTests(Supplier<TestGeneratorProfile> profile) {
		return provideConfiguration(TestGeneratorProfile.class, args -> profile.get());
	}

	public AgentConfigurator optimizeTimings(Supplier<PerformanceProfile> profile) {
		return provideConfiguration(PerformanceProfile.class, args -> profile.get());
	}

	public AgentConfigurator record(Supplier<SerializationProfile> profile) {
		return provideConfiguration(SerializationProfile.class, args -> profile.get());
	}

	public AgentConfigurator to(Function<AgentConfiguration, SnapshotConsumer> profile) {
		return provideConfiguration(SnapshotConsumer.class, args -> profile.apply((AgentConfiguration) args[0]));
	}

	public <T> AgentConfigurator provideConfiguration(Class<T> clazz, Function<Object[], T> value) {
		assert clazz.isAnnotationPresent(ExtensionPoint.class);
		configurationLoaders.computeIfAbsent(clazz, c -> new FixedConfigurationLoader())
			.provide(clazz, value);
		return this;
	}

	public AgentConfigurator fallbackTo(ConfigurationLoader configurationLoader) {
		this.fallbackLoaders.add(configurationLoader);
		return this;
	}

	public AgentConfigurator defaultSerializers() {
		configurationLoaders.computeIfAbsent(Serializer.class, c -> DefaultSerializers.defaults().stream()
			.reduce(new FixedConfigurationLoader(), (loader, generator) -> loader.provide(Serializer.class, generator), (l1,l2) -> l1));
		return this;
	}

	public AgentConfigurator customSerializer(Supplier<Serializer<?>> serializer) {
		provideConfiguration(Serializer.class, args -> serializer.get());
		return this;
	}

	public AgentConfigurator defaultSetupGenerators() {
		configurationLoaders.computeIfAbsent(SetupGenerator.class, c -> DefaultSetupGenerators.defaults().stream()
			.reduce(new FixedConfigurationLoader(), (loader, generator) -> loader.provide(SetupGenerator.class, generator), (l1,l2) -> l1));
		return this;
	}

	public AgentConfigurator customSetupGenerator(Supplier<SetupGenerator<?>> serializer) {
		provideConfiguration(SetupGenerator.class, args -> serializer.get());
		return this;
	}

	public AgentConfigurator defaultMatcherGenerators() {
		configurationLoaders.computeIfAbsent(MatcherGenerator.class, c -> DefaultMatcherGenerators.defaults().stream()
			.reduce(new FixedConfigurationLoader(), (loader, generator) -> loader.provide(MatcherGenerator.class, generator), (l1,l2) -> l1));
		return this;
	}

	public AgentConfigurator customMatcherGenerator(Supplier<MatcherGenerator<?>> serializer) {
		provideConfiguration(MatcherGenerator.class, args -> serializer.get());
		return this;
	}

	public AgentConfiguration configure() {
		List<ConfigurationLoader> loaders = new ArrayList<>();
		loaders.addAll(configurationLoaders.values());
		loaders.addAll(fallbackLoaders);
		return new AgentConfiguration(loaders)
			.withDefaultValue(SerializationProfile.class, DefaultSerializationProfile::new)
			.withDefaultValue(PerformanceProfile.class, DefaultPerformanceProfile::new)
			.withDefaultValue(TestGeneratorProfile.class, DefaultTestGeneratorProfile::new)
			.withDefaultValue(SnapshotConsumer.class, DefaultSnapshotConsumer::new);
	}

}
