package net.amygdalum.testrecorder;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ServiceLoader;

public class TestRecorderAgent {

	public static void agentmain(String agentArgs, Instrumentation inst) {
		premain(agentArgs, inst);
	}
	
	public static void premain(String agentArgs, Instrumentation inst) {
		TestRecorderAgentConfig config = loadConfig(agentArgs);

		inst.addTransformer(new SnapshotInstrumentor(config));

		try {
			inst.addTransformer(AllLambdasSerializableTransformer.INSTANCE, true);
			inst.retransformClasses(AllLambdasSerializableTransformer.INSTANCE.classesToRetransform());
		} catch (ClassNotFoundException | UnmodifiableClassException e) {
			System.out.println("unexpected class transforming restriction: " + e.getMessage());
		}

		initialize(config);
	}

	public static void initialize(TestRecorderAgentConfig config) {
		ServiceLoader<TestRecorderAgentInitializer> loader = ServiceLoader.load(TestRecorderAgentInitializer.class);

		for (TestRecorderAgentInitializer initializer : loader) {
			try {
				initializer.run();
			} catch (RuntimeException e) {
				System.out.println("initializer " + initializer.getClass().getSimpleName() + " failed with " + e.getMessage() + ", skipping");
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
