package net.amygdalum.testrecorder;

import java.lang.instrument.Instrumentation;

import net.amygdalum.testrecorder.profile.AgentConfiguration;
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
			String[] args = agentArgs.split(";");
			return new AgentConfiguration(args)
				.withDefaultValue(SerializationProfile.class, DefaultSerializationProfile::new)
				.withDefaultValue(PerformanceProfile.class, DefaultPerformanceProfile::new)
				.withDefaultValue(SnapshotConsumer.class, DefaultSnapshotConsumer::new);
		} else {
			return new AgentConfiguration()
				.withDefaultValue(SerializationProfile.class, DefaultSerializationProfile::new)
				.withDefaultValue(PerformanceProfile.class, DefaultPerformanceProfile::new)
				.withDefaultValue(SnapshotConsumer.class, DefaultSnapshotConsumer::new);
		}
	}

	public void prepareInstrumentations() {
		snapshotInstrumentor = new SnapshotInstrumentor(config).attach(inst);
		lambdaTransformer = new AllLambdasSerializableTransformer().attach(inst);

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