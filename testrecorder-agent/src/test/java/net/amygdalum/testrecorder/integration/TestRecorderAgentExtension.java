package net.amygdalum.testrecorder.integration;

import static java.util.stream.Collectors.toList;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import net.amygdalum.testrecorder.DefaultPerformanceProfile;
import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.DefaultSnapshotConsumer;
import net.amygdalum.testrecorder.SnapshotConsumer;
import net.amygdalum.testrecorder.TestAgentConfiguration;
import net.amygdalum.testrecorder.TestRecorderAgent;
import net.amygdalum.testrecorder.generator.TestGenerator;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.Classes;
import net.amygdalum.testrecorder.profile.ConfigurableSerializationProfile;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.profile.SerializationProfile;
import net.amygdalum.testrecorder.util.Logger;
import net.bytebuddy.agent.ByteBuddyAgent;

public class TestRecorderAgentExtension implements BeforeEachCallback, BeforeAllCallback, AfterAllCallback, ParameterResolver {

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
		TestGenerator fromRecorded = TestGenerator.fromRecorded();
		if (fromRecorded != null) {
			fromRecorded.clearResults();
		}
	}

	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return AgentConfiguration.class.isAssignableFrom(parameterContext.getParameter().getType());
	}

	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
		return agent.getConfig();
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
			return new TestAgentConfiguration()
				.withDefaultValue(SerializationProfile.class, DefaultSerializationProfile::new)
				.withDefaultValue(PerformanceProfile.class, DefaultPerformanceProfile::new)
				.withDefaultValue(SnapshotConsumer.class, DefaultSnapshotConsumer::new);
		}
		Class<? extends SerializationProfile> base = instrumented.config();
		List<Classes> classes = Arrays.stream(instrumented.classes())
			.map(Classes::byName)
			.collect(toList());
		SerializationProfile profile = ConfigurableSerializationProfile.builder(base.newInstance())
			.withClasses(classes)
			.build();
		AgentConfiguration config = new TestAgentConfiguration();
		return config
			.withDefaultValue(SerializationProfile.class, () -> profile)
			.withDefaultValue(PerformanceProfile.class, DefaultPerformanceProfile::new)
			.withDefaultValue(SnapshotConsumer.class, () -> new TestGenerator(config));
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
