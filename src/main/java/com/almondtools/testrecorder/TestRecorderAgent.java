package com.almondtools.testrecorder;

import java.lang.instrument.Instrumentation;

public class TestRecorderAgent {

	public static void premain(String agentArgs, Instrumentation inst) {
		SnapshotConfig config = loadConfig(agentArgs);
		inst.addTransformer(new SnapshotInstrumentor(config));
	}

	@SuppressWarnings("unchecked")
	private static SnapshotConfig loadConfig(String agentArgs) {
		try {
			Class<? extends SnapshotConfig> config = (Class<? extends SnapshotConfig>) Class.forName(agentArgs);
			System.out.println("loading config " + config.getSimpleName());
			return config.newInstance();
		} catch (RuntimeException | ReflectiveOperationException e) {
			System.out.println("loading default config");
			return new DefaultConfig();
		}
	}

}
