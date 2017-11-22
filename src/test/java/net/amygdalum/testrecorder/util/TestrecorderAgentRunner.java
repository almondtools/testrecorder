package net.amygdalum.testrecorder.util;

import static java.util.stream.Collectors.toList;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import net.amygdalum.testrecorder.Classes;
import net.amygdalum.testrecorder.ConfigurableTestRecorderAgentConfig;
import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;
import net.amygdalum.testrecorder.TestRecorderAgent;
import net.amygdalum.testrecorder.TestRecorderAgentConfig;
import net.amygdalum.testrecorder.runtime.FakeIO;
import net.bytebuddy.agent.ByteBuddyAgent;

public class TestrecorderAgentRunner extends BlockJUnit4ClassRunner {

	public static Instrumentation inst;
	private TestRecorderAgent agent;

	public TestrecorderAgentRunner(Class<?> klass) throws InitializationError {
		super(klass);
		if (inst == null) {
			inst = ByteBuddyAgent.install();
		}
	}

	@Override
	public void run(RunNotifier notifier) {
		try {
			setupTransformer();
			super.run(notifier);
		} catch (Exception e) {
			notifier.fireTestFailure(new Failure(getDescription(), e));
		} finally {
			resetTransformer();
		}
	}

	private void setupTransformer() {
		try {
			System.out.println("setup");
			Instrumented instrumented = getTestClass().getJavaClass().getAnnotation(Instrumented.class);

			TestRecorderAgentConfig config = fetchConfig(instrumented);

			Class<?>[] classes = fetchClasses(instrumented);

			agent = new TestRecorderAgent(inst);
			agent.prepareInstrumentations(config);
			if (classes.length > 0) {
				inst.retransformClasses(classes);
			}
			FakeIO.reset();
		} catch (ReflectiveOperationException | UnmodifiableClassException | Error e) {
			throw new RuntimeException(e);
		}
	}

	private TestRecorderAgentConfig fetchConfig(Instrumented instrumented) throws InstantiationException, IllegalAccessException {
		if (instrumented == null) {
			return new DefaultTestRecorderAgentConfig();
		}
		Class<? extends TestRecorderAgentConfig> base = instrumented.config();
		List<Classes> classes = Arrays.stream(instrumented.classes())
			.map(Classes::byName)
			.collect(toList());
		return ConfigurableTestRecorderAgentConfig.builder(base.newInstance())
			.withClasses(classes)
			.build();
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
		System.out.println("reset");
		if (agent != null) {
			agent.clearInstrumentations();
		}
		FakeIO.reset();
	}

}
