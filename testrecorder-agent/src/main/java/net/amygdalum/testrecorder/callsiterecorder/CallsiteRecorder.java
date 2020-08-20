package net.amygdalum.testrecorder.callsiterecorder;

import static java.util.stream.Collectors.toList;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.amygdalum.testrecorder.DefaultPerformanceProfile;
import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.DefaultSnapshotConsumer;
import net.amygdalum.testrecorder.TestRecorderAgent;
import net.amygdalum.testrecorder.TestrecorderThreadFactory;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.ClassPathConfigurationLoader;
import net.amygdalum.testrecorder.profile.Classes;
import net.amygdalum.testrecorder.profile.ConfigurableSerializationProfile;
import net.amygdalum.testrecorder.profile.DefaultPathConfigurationLoader;
import net.amygdalum.testrecorder.profile.FixedConfigurationLoader;
import net.amygdalum.testrecorder.profile.Methods;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.profile.SerializationProfile;
import net.amygdalum.testrecorder.profile.SnapshotConsumer;
import net.amygdalum.testrecorder.types.ContextSnapshot;
import net.bytebuddy.agent.ByteBuddyAgent;

public class CallsiteRecorder implements SnapshotConsumer, AutoCloseable {

	public static Instrumentation inst = ByteBuddyAgent.install();

	private ThreadPoolExecutor executor;

	private CompletableFuture<List<ContextSnapshot>> snapshots;

	private TestRecorderAgent agent;
	private Class<?>[] classes;

	private CallsiteRecorder(AgentConfiguration config, Class<?>[] classes) {
		this.agent = new TestRecorderAgent(inst, config);
		this.classes = classes;

		PerformanceProfile performanceProfile = config.loadConfiguration(PerformanceProfile.class);
		this.executor = new ThreadPoolExecutor(0, 1, performanceProfile.getIdleTime(), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new TestrecorderThreadFactory("$consume"));

		this.snapshots = CompletableFuture.supplyAsync(() -> new LinkedList<>(), executor);
	}

	public static CallsiteRecorder create(Method... methods) {
		return create(new DefaultSerializationProfile(), methods);
	}

	public static CallsiteRecorder create(SerializationProfile profile, Method... methods) {
		FixedConfigurationLoader fixedLoader = new FixedConfigurationLoader();
		ClassPathConfigurationLoader classpathLoader = new ClassPathConfigurationLoader();
		DefaultPathConfigurationLoader defaultPathLoader = new DefaultPathConfigurationLoader();
		AgentConfiguration config = new AgentConfiguration(fixedLoader, classpathLoader, defaultPathLoader)
			.withDefaultValue(SerializationProfile.class, DefaultSerializationProfile::new)
			.withDefaultValue(PerformanceProfile.class, DefaultPerformanceProfile::new)
			.withDefaultValue(SnapshotConsumer.class, DefaultSnapshotConsumer::new);
		Class<?>[] classes = Arrays.stream(methods)
			.map(Method::getDeclaringClass)
			.toArray(Class[]::new);
		CallsiteRecorder recorder = new CallsiteRecorder(config, classes);
		
		fixedLoader
			.provide(SerializationProfile.class, ConfigurableSerializationProfile.builder(profile)
				.withClasses(Arrays.stream(methods)
					.map(Method::getDeclaringClass)
					.map(Classes::byDescription)
					.collect(toList()))
				.withRecorded(Arrays.stream(methods)
					.map(Methods::byDescription)
					.collect(toList()))
				.build())
			.provide(SnapshotConsumer.class, recorder);

		return recorder.init();
	}

	@SuppressWarnings("resource")
	public static CallsiteRecorder create(AgentConfiguration config, Class<?>[] classes) {
		CallsiteRecorder recorder = new CallsiteRecorder(config, classes);
		return recorder
			.init();
	}

	private CallsiteRecorder init() {
		this.agent.prepareInstrumentations();
		try {
			inst.retransformClasses(classes);
		} catch (UnmodifiableClassException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public CompletableFuture<List<ContextSnapshot>> record(Runnable runnable) {
		runnable.run();
		return snapshots;
	}

	public <T> T record(Callable<T> callable) throws Exception {
		return callable.call();
	}

	public synchronized CompletableFuture<List<ContextSnapshot>> snapshots() {
		return snapshots;
	}

	@Override
	public synchronized void accept(ContextSnapshot snapshot) {
		this.snapshots = this.snapshots.thenApply(list -> {
			list.add(snapshot);
			return list;
		});
	}

	@Override
	public void close() throws Exception {
		if (agent != null) {
			agent.clearInstrumentations();
		}
	}

}
