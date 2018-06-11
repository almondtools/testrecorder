package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.toList;

import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.ClassPathConfigurationLoader;
import net.amygdalum.testrecorder.profile.DefaultPathConfigurationLoader;
import net.amygdalum.testrecorder.profile.PathConfigurationLoader;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.profile.SerializationProfile;
import net.amygdalum.testrecorder.runtime.TestRecorderAgentInitializer;
import net.amygdalum.testrecorder.util.AttachableClassFileTransformer;
import net.amygdalum.testrecorder.util.Logger;

public class TestRecorderAgent {

	private Instrumentation inst;
	private AgentConfiguration config;
	private AttachableClassFileTransformer lambdaTransformer;
	private AttachableClassFileTransformer snapshotInstrumentor;

	public TestRecorderAgent(Instrumentation inst, AgentConfiguration config) {
		this.inst = inst;
		this.config = config;
	}

	public AgentConfiguration getConfig() {
		return config;
	}

	public static void agentmain(String agentArgs, Instrumentation inst) {
		AgentConfiguration config = loadConfig(agentArgs);

		new TestRecorderAgent(inst, config).prepareInstrumentations();
	}

	public static void premain(String agentArgs, Instrumentation inst) {
		AgentConfiguration config = loadConfig(agentArgs);

		new TestRecorderAgent(inst, config).prepareInstrumentations();
	}

	protected static AgentConfiguration loadConfig(String agentArgs) {
		if (agentArgs != null) {
			List<Path> paths = Arrays.stream(agentArgs.split(";"))
				.map(path -> Paths.get(path))
				.collect(toList());

			return new AgentConfiguration(new PathConfigurationLoader(paths), new ClassPathConfigurationLoader(), new DefaultPathConfigurationLoader())
				.withDefaultValue(SerializationProfile.class, DefaultSerializationProfile::new)
				.withDefaultValue(PerformanceProfile.class, DefaultPerformanceProfile::new)
				.withDefaultValue(SnapshotConsumer.class, DefaultSnapshotConsumer::new);
		} else {
			return new AgentConfiguration(new ClassPathConfigurationLoader(), new DefaultPathConfigurationLoader())
				.withDefaultValue(SerializationProfile.class, DefaultSerializationProfile::new)
				.withDefaultValue(PerformanceProfile.class, DefaultPerformanceProfile::new)
				.withDefaultValue(SnapshotConsumer.class, DefaultSnapshotConsumer::new);
		}
	}

	public void prepareInstrumentations() {
		lambdaTransformer = new AllLambdasSerializableTransformer().attach(inst);
		snapshotInstrumentor = new SnapshotInstrumentor(config).attach(inst);

		initialize();
	}

	public void initialize() {
		for (TestRecorderAgentInitializer initializer : config.loadConfigurations(TestRecorderAgentInitializer.class)) {
			try {
				initializer.run();
			} catch (RuntimeException e) {
				Logger.error("initializer " + initializer.getClass().getSimpleName() + " failed with " + e.getMessage() + ", skipping", e);
			}
		}
	}

	public void clearInstrumentations() {
		if (snapshotInstrumentor != null) {
			snapshotInstrumentor.detach(inst);
		}
		if (lambdaTransformer != null) {
			lambdaTransformer.detach(inst);
		}
	}

}