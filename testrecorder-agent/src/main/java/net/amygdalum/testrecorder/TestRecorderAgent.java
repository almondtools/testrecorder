package net.amygdalum.testrecorder;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.ClassPathConfigurationLoader;
import net.amygdalum.testrecorder.profile.DefaultPathConfigurationLoader;
import net.amygdalum.testrecorder.profile.PathConfigurationLoader;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.profile.SerializationProfile;
import net.amygdalum.testrecorder.profile.SnapshotConsumer;
import net.amygdalum.testrecorder.util.AttachableClassFileTransformer;

public class TestRecorderAgent {

	private Instrumentation inst;
	private AgentConfiguration config;
	private List<Class<? extends AttachableClassFileTransformer>> transformerClasses;
	private Deque<AttachableClassFileTransformer> transformers;

	public TestRecorderAgent(Instrumentation inst, AgentConfiguration config, List<Class<? extends AttachableClassFileTransformer>> transformerClasses) {
		this.inst = inst;
		this.config = config;
		this.transformerClasses = transformerClasses;
	}

	public TestRecorderAgent(Instrumentation inst, AgentConfiguration config) {
		this(inst, config, asList(AllLambdasSerializableTransformer.class, SnapshotInstrumentor.class));
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
		transformers = new LinkedList<>();
		
		for (Class<? extends AttachableClassFileTransformer> clazz : transformerClasses) {
			AttachableClassFileTransformer transformer = instantiate(clazz);
			transformer.attach(inst);
			transformers.add(transformer);
		}
	}

	public AttachableClassFileTransformer instantiate(Class<? extends AttachableClassFileTransformer> clazz) {
		try {
			return clazz.getDeclaredConstructor(AgentConfiguration.class)
				.newInstance(config);
		} catch (ReflectiveOperationException e) {
			//try next
		}
		try {
			return clazz.newInstance();
		} catch (ReflectiveOperationException e) {
			//try next
		}
		throw new RuntimeException("failed to instantiate transformer <" + clazz.getName() + ">, tried:"
			+ "\nnew " + clazz.getSimpleName() + "(AgentConfig)"
			+ "\nnew " + clazz.getSimpleName() + "()");
	}

	public void clearInstrumentations() {
		if (transformers == null) {
			return;
		}
		while (!transformers.isEmpty()) {
			AttachableClassFileTransformer current = transformers.removeLast();
			current.detach(inst);
		}
	}

	public void withoutInstrumentation(Runnable runnable) {
		try {
			clearInstrumentations();
			runnable.run();
		} finally {
			prepareInstrumentations();
		}

	}

}