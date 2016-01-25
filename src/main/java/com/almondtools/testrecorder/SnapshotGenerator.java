package com.almondtools.testrecorder;

import static com.almondtools.testrecorder.ConfigRegistry.loadConfig;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class SnapshotGenerator {

	private static ThreadFactory THREADS = new ThreadFactory() {

		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = Executors.defaultThreadFactory().newThread(runnable);
			thread.setDaemon(true);
			return thread;
		}

	};
	
	private static ThreadLocal<SnapshotGenerator> currentGenerator = new ThreadLocal<>();

	private ThreadLocal<SnapshotGenerator> stored = new ThreadLocal<>();
	
	private Object self;

	private ExecutorService executor;
	private Map<String, ContextSnapshotFactory> methodSnapshots;
	private ThreadLocal<SnapshotProcess> current = new ThreadLocal<>();

	private SnapshotConsumer snapshotConsumer;
	private long timeoutInMillis;

	public SnapshotGenerator(Object self, Class<? extends SnapshotConfig> config) {
		this.snapshotConsumer = loadConfig(config).getSnapshotConsumer();
		this.timeoutInMillis = loadConfig(config).getTimeoutInMillis();

		this.self = self;

		this.executor = Executors.newSingleThreadExecutor(THREADS);
		this.methodSnapshots = new HashMap<>();
	}
	
	public static SnapshotGenerator getCurrentGenerator() {
		return currentGenerator.get();
	}

	public SnapshotConsumer getMethodConsumer() {
		return snapshotConsumer;
	}

	public void register(String signature, Method method) {
		ContextSnapshotFactory factory = new ContextSnapshotFactory(method.getDeclaringClass(), method.getAnnotation(Snapshot.class), method.getGenericReturnType(), method.getName(),
			method.getGenericParameterTypes());
		methodSnapshots.put(signature, factory);
	}

	public SnapshotProcess process(ContextSnapshotFactory factory) {
		SnapshotProcess process = new SnapshotProcess(executor, timeoutInMillis, factory);
		current.set(process);
		return process;
	}

	public SnapshotProcess process() {
		return current.get();
	}

	public void setupVariables(String signature, Object... args) {
		init();
		ContextSnapshotFactory factory = methodSnapshots.get(signature);
		SnapshotProcess process = process(factory);
		process.setupVariables(signature, self, args);
	}

	public void init() {
		stored.set(currentGenerator.get());
		currentGenerator.set(this);
	}

	public void inputVariables(Class<?> clazz, String method, Type resultType, Object result, Type[] paramTypes, Object... args) {
		SnapshotProcess process = process();
		process.inputVariables(clazz, method, resultType, result, paramTypes, args);
	}

	public void inputVariables(Class<?> clazz, String method, Type[] paramTypes, Object... args) {
		SnapshotProcess process = process();
		process.inputVariables(clazz, method, paramTypes, args);
	}

	public void outputVariables(Class<?> clazz, String method, Type[] paramTypes, Object... args) {
		SnapshotProcess process = process();
		process.outputVariables(clazz, method, paramTypes, args);
	}

	public void expectVariables(Object result, Object... args) {
		SnapshotProcess process = process();
		process.expectVariables(self, result, args);
		done();
		consume(process.getSnapshot());
	}

	public void expectVariables(Object... args) {
		SnapshotProcess process = process();
		process.expectVariables(self, args);
		done();
		consume(process.getSnapshot());
	}

	public void throwVariables(Throwable throwable, Object... args) {
		SnapshotProcess process = process();
		process.throwVariables(self, throwable, args);
		done();
		consume(process.getSnapshot());
	}

	public void done() {
		currentGenerator.set(stored.get());
		stored.remove();
	}

	private void consume(ContextSnapshot snapshot) {
		if (snapshot.isValid()) {
			if (snapshotConsumer != null) {
				snapshotConsumer.accept(snapshot);
			}
		}
	}

}
