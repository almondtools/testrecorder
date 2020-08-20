package net.amygdalum.testrecorder.configurator;

import static java.util.stream.Collectors.toList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.amygdalum.testrecorder.DefaultPerformanceProfile;
import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.DefaultSnapshotConsumer;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerator;
import net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerator;
import net.amygdalum.testrecorder.extensionpoint.ExtensionPoint;
import net.amygdalum.testrecorder.generator.DefaultTestGeneratorProfile;
import net.amygdalum.testrecorder.generator.TestGeneratorProfile;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.FixedConfigurationLoader;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.profile.SerializationProfile;
import net.amygdalum.testrecorder.profile.SnapshotConsumer;
import net.amygdalum.testrecorder.types.Serializer;

public class AgentConfigurator {

	private Map<Class<?>, FixedConfigurationLoader> configurationLoaders;

	public AgentConfigurator() {
		this.configurationLoaders = new LinkedHashMap<>();
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

	public AgentConfigurator defaultSerializers() {
		configurationLoaders.computeIfAbsent(Serializer.class, c -> new FixedConfigurationLoader())
			.provide(Serializer.class, args -> new net.amygdalum.testrecorder.serializers.ArraysListSerializer())
			.provide(Serializer.class, args -> new net.amygdalum.testrecorder.serializers.CollectionsListSerializer())
			.provide(Serializer.class, args -> new net.amygdalum.testrecorder.serializers.CollectionsSetSerializer())
			.provide(Serializer.class, args -> new net.amygdalum.testrecorder.serializers.CollectionsMapSerializer())
			.provide(Serializer.class, args -> new net.amygdalum.testrecorder.serializers.DefaultListSerializer())
			.provide(Serializer.class, args -> new net.amygdalum.testrecorder.serializers.DefaultQueueSerializer())
			.provide(Serializer.class, args -> new net.amygdalum.testrecorder.serializers.DefaultDequeSerializer())
			.provide(Serializer.class, args -> new net.amygdalum.testrecorder.serializers.DefaultSetSerializer())
			.provide(Serializer.class, args -> new net.amygdalum.testrecorder.serializers.DefaultMapSerializer())
			.provide(Serializer.class, args -> new net.amygdalum.testrecorder.serializers.ClassSerializer())
			.provide(Serializer.class, args -> new net.amygdalum.testrecorder.serializers.BigIntegerSerializer())
			.provide(Serializer.class, args -> new net.amygdalum.testrecorder.serializers.BigDecimalSerializer());
		return this;
	}

	public AgentConfigurator customSerializer(Supplier<Serializer<?>> serializer) {
		provideConfiguration(Serializer.class, args -> serializer.get());
		return this;
	}

	public AgentConfigurator defaultSetupGenerators() {
		configurationLoaders.computeIfAbsent(SetupGenerator.class, c -> new FixedConfigurationLoader())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.DefaultLiteralAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.DefaultNullAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.DefaultClassAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.DefaultBigIntegerAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.DefaultBigDecimalAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.DefaultEnumAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.DefaultLambdaAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.DefaultProxyAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.ProxyPlaceholderAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.ObjectBuilderAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.ObjectFactoryAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.BeanObjectAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.DefaultObjectAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.DefaultArrayAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.ArraysListAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.CollectionsListAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.DefaultListAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.DefaultQueueAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.CollectionsSetAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.DefaultSetAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.CollectionsMapAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.DefaultMapAdaptor())
			.provide(SetupGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.builder.LargePrimitiveArrayAdaptor());
		return this;
	}

	public AgentConfigurator customSetupGenerator(Supplier<SetupGenerator<?>> serializer) {
		provideConfiguration(SetupGenerator.class, args -> serializer.get());
		return this;
	}

	public AgentConfigurator defaultMatcherGenerators() {
		configurationLoaders.computeIfAbsent(MatcherGenerator.class, c -> new FixedConfigurationLoader())
			.provide(MatcherGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.matcher.DefaultLiteralAdaptor())
			.provide(MatcherGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.matcher.DefaultNullAdaptor())
			.provide(MatcherGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.matcher.DefaultClassAdaptor())
			.provide(MatcherGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.matcher.DefaultBigIntegerAdaptor())
			.provide(MatcherGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.matcher.DefaultBigDecimalAdaptor())
			.provide(MatcherGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.matcher.DefaultEnumAdaptor())
			.provide(MatcherGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.matcher.DefaultLambdaAdaptor())
			.provide(MatcherGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.matcher.DefaultProxyAdaptor())
			.provide(MatcherGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.matcher.DefaultObjectAdaptor())
			.provide(MatcherGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.matcher.DefaultArrayAdaptor())
			.provide(MatcherGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.matcher.DefaultSequenceAdaptor())
			.provide(MatcherGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.matcher.DefaultSetAdaptor())
			.provide(MatcherGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.matcher.DefaultMapAdaptor())
			.provide(MatcherGenerator.class, args -> new net.amygdalum.testrecorder.deserializers.matcher.LargePrimitiveArrayAdaptor());
		return this;
	}

	public AgentConfigurator customMatcherGenerator(Supplier<MatcherGenerator<?>> serializer) {
		provideConfiguration(MatcherGenerator.class, args -> serializer.get());
		return this;
	}

	public AgentConfiguration configure() {
		configurationLoaders.computeIfAbsent(SerializationProfile.class, c -> new FixedConfigurationLoader()
			.provide(SerializationProfile.class, args -> new DefaultSerializationProfile()));
		configurationLoaders.computeIfAbsent(PerformanceProfile.class, c -> new FixedConfigurationLoader()
			.provide(PerformanceProfile.class, args -> new DefaultPerformanceProfile()));
		configurationLoaders.computeIfAbsent(TestGeneratorProfile.class, c -> new FixedConfigurationLoader()
			.provide(TestGeneratorProfile.class, args -> new DefaultTestGeneratorProfile()));
		configurationLoaders.computeIfAbsent(SnapshotConsumer.class, c -> new FixedConfigurationLoader()
			.provide(SnapshotConsumer.class, args -> new DefaultSnapshotConsumer()));

		return new AgentConfiguration(configurationLoaders.values().stream()
			.collect(toList()));
	}

}
