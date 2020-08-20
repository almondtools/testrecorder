package net.amygdalum.testrecorder.configurator;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.amygdalum.testrecorder.DefaultPerformanceProfile;
import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.DefaultSnapshotConsumer;
import net.amygdalum.testrecorder.deserializers.builder.SetupGenerator;
import net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerator;
import net.amygdalum.testrecorder.generator.DefaultTestGeneratorProfile;
import net.amygdalum.testrecorder.generator.TestGeneratorProfile;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.profile.SerializationProfile;
import net.amygdalum.testrecorder.profile.SnapshotConsumer;
import net.amygdalum.testrecorder.types.Serializer;

public class AgentConfiguratorTest {

	@Test
	void testAgentConfigurator() throws Exception {
		AgentConfiguration config = new AgentConfigurator()
			.configure();

		assertThat(config.loadConfiguration(TestGeneratorProfile.class)).isInstanceOf(DefaultTestGeneratorProfile.class);
		assertThat(config.loadConfiguration(PerformanceProfile.class)).isInstanceOf(DefaultPerformanceProfile.class);
		assertThat(config.loadConfiguration(SerializationProfile.class)).isInstanceOf(DefaultSerializationProfile.class);
		assertThat(config.loadConfiguration(SnapshotConsumer.class, config)).isInstanceOf(DefaultSnapshotConsumer.class);
		assertThat(config.loadConfigurations(Serializer.class, config)).isEmpty();
		assertThat(config.loadConfigurations(SetupGenerator.class, config)).isEmpty();
		assertThat(config.loadConfigurations(MatcherGenerator.class, config)).isEmpty();
	}

	@Test
	void testGenerateTests() {
		TestGeneratorProfile profile = Mockito.mock(TestGeneratorProfile.class);
		AgentConfiguration config = new AgentConfigurator()
			.generateTests(() -> profile)
			.configure();

		assertThat(config.loadConfiguration(TestGeneratorProfile.class)).isSameAs(profile);
	}

	@Test
	void testOptimizeTimings() {
		PerformanceProfile profile = Mockito.mock(PerformanceProfile.class);
		AgentConfiguration config = new AgentConfigurator()
			.optimizeTimings(() -> profile)
			.configure();

		assertThat(config.loadConfiguration(PerformanceProfile.class)).isSameAs(profile);
	}

	@Test
	void testRecord() {
		SerializationProfile profile = Mockito.mock(SerializationProfile.class);
		AgentConfiguration config = new AgentConfigurator()
			.record(() -> profile)
			.configure();

		assertThat(config.loadConfiguration(SerializationProfile.class)).isSameAs(profile);
	}

	@Test
	void testTo() {
		SnapshotConsumer snapshotConsumer = Mockito.mock(SnapshotConsumer.class);
		AgentConfiguration config = new AgentConfigurator()
			.to(c -> snapshotConsumer)
			.configure();

		assertThat(config.loadConfiguration(SnapshotConsumer.class, config)).isSameAs(snapshotConsumer);
	}

	@Test
	void testDefaultSerializers() throws Exception {
		AgentConfiguration config = new AgentConfigurator()
			.defaultSerializers()
			.configure();
		List<String> expected = configurationsFrom("src/main/resources/agentconfig/net.amygdalum.testrecorder.types.Serializer");

		assertThat(config.loadConfigurations(Serializer.class)).extracting(s -> s.getClass().getName())
			.containsExactlyElementsOf(expected);
	}

	@Test
	void testCustomSerializer() throws Exception {
		Serializer<?> serializer = Mockito.mock(Serializer.class);
		AgentConfiguration config = new AgentConfigurator()
			.customSerializer(() -> serializer)
			.configure();

		assertThat(config.loadConfigurations(Serializer.class))
			.containsExactly(serializer);
	}

	@Test
	void testDefaultSetupGenerator() throws Exception {
		AgentConfiguration config = new AgentConfigurator()
			.defaultSetupGenerators()
			.configure();
		List<String> expected = configurationsFrom("src/main/resources/agentconfig/net.amygdalum.testrecorder.deserializers.builder.SetupGenerator");

		assertThat(config.loadConfigurations(SetupGenerator.class)).extracting(s -> s.getClass().getName())
			.containsExactlyElementsOf(expected);
	}

	@Test
	void testCustomSetupGenerator() throws Exception {
		SetupGenerator<?> generator = Mockito.mock(SetupGenerator.class);
		AgentConfiguration config = new AgentConfigurator()
			.customSetupGenerator(() -> generator)
			.configure();

		assertThat(config.loadConfigurations(SetupGenerator.class))
			.containsExactly(generator);
	}

	@Test
	void testDefaultMatcherGenerator() throws Exception {
		AgentConfiguration config = new AgentConfigurator()
			.defaultMatcherGenerators()
			.configure();
		List<String> expected = configurationsFrom("src/main/resources/agentconfig/net.amygdalum.testrecorder.deserializers.matcher.MatcherGenerator");

		assertThat(config.loadConfigurations(MatcherGenerator.class)).extracting(s -> s.getClass().getName())
			.containsExactlyElementsOf(expected);
	}

	@Test
	void testCustomMatcherGenerator() throws Exception {
		MatcherGenerator<?> generator = Mockito.mock(MatcherGenerator.class);
		AgentConfiguration config = new AgentConfigurator()
			.customMatcherGenerator(() -> generator)
			.configure();

		assertThat(config.loadConfigurations(MatcherGenerator.class))
			.containsExactly(generator);
	}

	private List<String> configurationsFrom(String pathname) throws IOException {
		return Files.lines(Paths.get(pathname))
			.map(line -> line.trim())
			.filter(line -> !line.isEmpty() && !line.startsWith("#"))
			.collect(toList());
	}

}
