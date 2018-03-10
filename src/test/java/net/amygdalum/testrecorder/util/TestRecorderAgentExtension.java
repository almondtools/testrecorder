package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.toList;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import net.amygdalum.testrecorder.AgentConfiguration;
import net.amygdalum.testrecorder.ConfigurableTestRecorderAgentConfig;
import net.amygdalum.testrecorder.ConfigurableTestRecorderAgentConfig.Builder;
import net.amygdalum.testrecorder.TestGenerator;
import net.amygdalum.testrecorder.TestRecorderAgent;
import net.amygdalum.testrecorder.TestRecorderAgentConfig;
import net.amygdalum.testrecorder.profile.Classes;
import net.bytebuddy.agent.ByteBuddyAgent;

public class TestRecorderAgentExtension implements BeforeEachCallback, BeforeAllCallback, AfterAllCallback {

	public static Instrumentation inst = ByteBuddyAgent.install();

	private TestRecorderAgent agent;

	public TestRecorderAgentExtension() {
	}

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		setupTransformer(context.getRequiredTestClass());
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		resetTransformer();
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		TestGenerator.fromRecorded().clearResults();
	}

	private void setupTransformer(Class<?> testClass) {
		try {
			Logger.info("setup");
			Instrumented instrumented = testClass.getAnnotation(Instrumented.class);

			AgentConfiguration config = fetchConfig(instrumented);

			Class<?>[] classes = fetchClasses(instrumented);

			agent = new TestRecorderAgent(inst, config);
			agent.prepareInstrumentations();
			if (classes.length > 0) {
				inst.retransformClasses(classes);
			}
		} catch (ReflectiveOperationException | UnmodifiableClassException | Error e) {
			throw new RuntimeException(e);
		}
	}

	private AgentConfiguration fetchConfig(Instrumented instrumented) throws InstantiationException, IllegalAccessException {
		if (instrumented == null) {
			return new TestAgentConfiguration();
		}
		Class<? extends TestRecorderAgentConfig> base = instrumented.config();
		List<Classes> classes = Arrays.stream(instrumented.classes())
			.map(Classes::byName)
			.collect(toList());
		TestRecorderAgentConfig baseConfig = base.newInstance();
		Builder config = ConfigurableTestRecorderAgentConfig.builder(baseConfig);
		return new TestAgentConfiguration(config
			.withClasses(classes)
			.build());
	}

	private Class<?>[] fetchClasses(Instrumented instrumented) throws ClassNotFoundException {
		if (instrumented == null) {
			return new Class[0];
		}
		String[] classNames = instrumented.classes();
		Class<?>[] classes = new Class[classNames.length];
		for (int i = 0; i < classes.length; i++) {
			classes[i] = Class.forName(classNames[i]);
		}
		return classes;
	}

	private void resetTransformer() {
		Logger.info("reset");
		if (agent != null) {
			agent.clearInstrumentations();
		}
	}

}
