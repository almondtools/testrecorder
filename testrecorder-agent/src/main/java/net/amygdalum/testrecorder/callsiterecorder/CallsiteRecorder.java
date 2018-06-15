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

import net.amygdalum.testrecorder.ContextSnapshot;
import net.amygdalum.testrecorder.DefaultPerformanceProfile;
import net.amygdalum.testrecorder.DefaultSerializationProfile;
import net.amygdalum.testrecorder.DefaultSnapshotConsumer;
import net.amygdalum.testrecorder.SnapshotConsumer;
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
import net.bytebuddy.agent.ByteBuddyAgent;

public class CallsiteRecorder implements SnapshotConsumer, AutoCloseable {

	public static Instrumentation inst = ByteBuddyAgent.install();

	private ConfigurableSerializationProfile profile;
	private AgentConfiguration config;
	private ThreadPoolExecutor executor;

	private CompletableFuture<List<ContextSnapshot>> snapshots;

	private TestRecorderAgent agent;


	public CallsiteRecorder(Method... methods) {
		this(new DefaultSerializationProfile(), methods);
	}
	
	public CallsiteRecorder(SerializationProfile profile, Method... methods) {
		this.profile = ConfigurableSerializationProfile.builder(profile)
			.withClasses(Arrays.stream(methods)
				.map(Method::getDeclaringClass)
				.map(Classes::byDescription)
				.collect(toList()))
			.withRecorded(Arrays.stream(methods)
				.map(Methods::byDescription)
				.collect(toList()))
			.build();
		this.config = new AgentConfiguration(
			new FixedConfigurationLoader()
				.provide(SerializationProfile.class, this.profile)
				.provide(SnapshotConsumer.class, this),
			new ClassPathConfigurationLoader(),
			new DefaultPathConfigurationLoader())
				.withDefaultValue(SerializationProfile.class, DefaultSerializationProfile::new)
				.withDefaultValue(PerformanceProfile.class, DefaultPerformanceProfile::new)
				.withDefaultValue(SnapshotConsumer.class, DefaultSnapshotConsumer::new);
		
		agent = new TestRecorderAgent(inst, config);
		agent.prepareInstrumentations();
		try {
			Class<?>[] classes = Arrays.stream(methods)
			.map(Method::getDeclaringClass).toArray(Class[]::new);
			inst.retransformClasses(classes);
		} catch (UnmodifiableClassException e) {
			throw new RuntimeException(e);
		}

		PerformanceProfile performanceProfile = config.loadConfiguration(PerformanceProfile.class);
		this.executor = new ThreadPoolExecutor(0, 1, performanceProfile.getIdleTime(), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new TestrecorderThreadFactory("$consume"));

		this.snapshots = CompletableFuture.supplyAsync(() -> new LinkedList<>(), executor);
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
