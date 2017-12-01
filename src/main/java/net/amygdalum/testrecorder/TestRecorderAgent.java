package net.amygdalum.testrecorder;

import java.lang.instrument.Instrumentation;
import java.util.ServiceLoader;

import net.amygdalum.testrecorder.util.AttachableClassFileTransformer;

public class TestRecorderAgent {

	private Instrumentation inst;
	private AttachableClassFileTransformer lambdaTransformer;
	private AttachableClassFileTransformer snapshotInstrumentor;

	public TestRecorderAgent(Instrumentation inst) {
		this.inst = inst;
	}

	public static void agentmain(String agentArgs, Instrumentation inst) {
		TestRecorderAgentConfig config = loadConfig(agentArgs);

		new TestRecorderAgent(inst).prepareInstrumentations(config);
	}

	public static void premain(String agentArgs, Instrumentation inst) {
		TestRecorderAgentConfig config = loadConfig(agentArgs);

		new TestRecorderAgent(inst).prepareInstrumentations(config);
	}

	@SuppressWarnings("unchecked")
	private static TestRecorderAgentConfig loadConfig(String agentArgs) {
		try {
			Class<? extends TestRecorderAgentConfig> config = (Class<? extends TestRecorderAgentConfig>) Class.forName(agentArgs);
			System.out.println("loading config " + config.getSimpleName());
			return config.newInstance();
		} catch (RuntimeException | ReflectiveOperationException e) {
			System.err.println("failed loading config " + agentArgs + ": " + e.getMessage());
			e.printStackTrace(System.err);
			System.out.println("loading default config");
			return new DefaultTestRecorderAgentConfig();
		}
	}

	public void prepareInstrumentations(TestRecorderAgentConfig config) {
		snapshotInstrumentor = new SnapshotInstrumentor(config).attach(inst);
		lambdaTransformer = new AllLambdasSerializableTransformer().attach(inst);

		initialize(config);
	}

	public void initialize(TestRecorderAgentConfig config) {
		ServiceLoader<TestRecorderAgentInitializer> loader = ServiceLoader.load(TestRecorderAgentInitializer.class);

		for (TestRecorderAgentInitializer initializer : loader) {
			try {
				initializer.run();
			} catch (RuntimeException e) {
				System.err.println("initializer " + initializer.getClass().getSimpleName() + " failed with " + e.getMessage() + ", skipping");
				e.printStackTrace(System.err);
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