package net.amygdalum.testrecorder;

import java.lang.instrument.Instrumentation;

public class TestRecorderAgent {

	public static void premain(String agentArgs, Instrumentation inst) {
		TestRecorderAgentConfig config = loadConfig(agentArgs);
		inst.addTransformer(new SnapshotInstrumentor(config));
		initialize(config.getInitializer());
	}

	public static void initialize(Class<? extends Runnable> initializerClass) {
		if (initializerClass != null) {
			try {
				Runnable initializer = initializerClass.newInstance();
				initializer.run();
			} catch (RuntimeException | ReflectiveOperationException e) {
				System.out.println("initializer failed with " + e.getMessage() + ", skipping");
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static TestRecorderAgentConfig loadConfig(String agentArgs) {
		try {
			Class<? extends TestRecorderAgentConfig> config = (Class<? extends TestRecorderAgentConfig>) Class.forName(agentArgs);
			System.out.println("loading config " + config.getSimpleName());
			return config.newInstance();
		} catch (RuntimeException | ReflectiveOperationException e) {
			System.out.println("loading default config");
			return new DefaultTestRecorderAgentConfig();
		}
	}

}
