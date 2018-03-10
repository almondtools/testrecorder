package net.amygdalum.testrecorder;

import java.lang.instrument.Instrumentation;

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

	public static void agentmain(String agentArgs, Instrumentation inst) {
		AgentConfiguration config = loadConfig(agentArgs);

		new TestRecorderAgent(inst, config).prepareInstrumentations();
	}

	public static void premain(String agentArgs, Instrumentation inst) {
		AgentConfiguration config = loadConfig(agentArgs);

		new TestRecorderAgent(inst, config).prepareInstrumentations();
	}

	protected static AgentConfiguration loadConfig(String agentArgs) {
		return loadConfig(agentArgs, TestRecorderAgent.class.getClassLoader());
	}
	
	protected static AgentConfiguration loadConfig(String agentArgs, ClassLoader loader) {
		if (agentArgs != null) {
			String[] args = agentArgs.split(";");
			return new AgentConfiguration(loader, args);
		} else {
			return new AgentConfiguration(loader);
		}
	}

	public void prepareInstrumentations() {
		TestRecorderAgentConfig instrumentationConfig = loadInstrumentationConfig();
		snapshotInstrumentor = new SnapshotInstrumentor(instrumentationConfig).attach(inst);
		lambdaTransformer = new AllLambdasSerializableTransformer().attach(inst);

		initialize();
	}

	protected TestRecorderAgentConfig loadInstrumentationConfig() {
		return config.loadConfiguration(TestRecorderAgentConfig.class)
			.orElseGet(this::loadDefaultConfig);
	}
	
	private TestRecorderAgentConfig loadDefaultConfig() {
		return config.loadDefaultConfiguration(new DefaultTestRecorderAgentConfig());
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