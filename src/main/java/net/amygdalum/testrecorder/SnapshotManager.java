package net.amygdalum.testrecorder;

import static java.lang.Thread.currentThread;
import static net.amygdalum.testrecorder.SnapshotProcess.PASSIVE;
import static net.amygdalum.testrecorder.TestrecorderThreadFactory.RECORDING;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SnapshotManager {

	public static SnapshotManager MANAGER;

	private ExecutorService snapshot;

	private Map<String, ContextSnapshotFactory> methodSnapshots;

	private ThreadLocal<Deque<SnapshotProcess>> current = ThreadLocal.withInitial(() -> newStack());

	private TestRecorderAgentConfig config;

	public SnapshotManager(TestRecorderAgentConfig config) {
		this.config = new FixedTestRecorderAgentConfig(config);

		this.snapshot = Executors.newSingleThreadExecutor(new TestrecorderThreadFactory(true, "$snapshot"));
		this.methodSnapshots = new HashMap<>();
	}

	public static SnapshotManager init(TestRecorderAgentConfig config) {
		MANAGER = new SnapshotManager(config);
		return MANAGER;
	}

	public SnapshotConsumer getMethodConsumer() {
		return config.getSnapshotConsumer();
	}

	public void register(String signature, Method method) {
		Class<?> declaringClass = method.getDeclaringClass();
		SerializationProfile profile = createProfileFor(method.getAnnotation(Snapshot.class));
		Type returnType = method.getGenericReturnType();
		String name = method.getName();
		Type[] parameterTypes = method.getGenericParameterTypes();
		
		ContextSnapshotFactory factory = new ContextSnapshotFactory(declaringClass, profile, returnType, name, parameterTypes);
		
		methodSnapshots.put(signature, factory);
	}

	private SerializationProfile createProfileFor(Snapshot snapshot) {
		if (snapshot == null) {
			return config;
		}
		Class<? extends SerializationProfile> profile = snapshot.profile();
		if (profile == null || profile.isInterface()) {
			return config;
		}
		try {
			return new DefaultingSerializationProfile(profile.newInstance(), config);
		} catch (InstantiationException | IllegalAccessException | NullPointerException e) {
			return config;
		}
	}


	public SnapshotProcess push(String signature) {
		ContextSnapshotFactory factory = methodSnapshots.get(signature);
		SnapshotProcess process = new SnapshotProcess(snapshot, config.getTimeoutInMillis(), factory);
		current.get().push(process);
		return process;
	}

	public SnapshotProcess current() {
		Deque<SnapshotProcess> stack = current.get();
		if (stack.isEmpty()) {
			return SnapshotProcess.PASSIVE;
		} else {
			return stack.peek();
		}
	}

	public SnapshotProcess pop() {
		return current.get().pop();
	}

	public void setupVariables(Object self, String signature, Object... args) {
		SnapshotProcess process = push(signature);
		process.setupVariables(signature, self, args);
	}

	public void inputVariables(Class<?> clazz, String method, Type resultType, Object result, Type[] paramTypes, Object... args) {
		current().inputVariables(clazz, method, resultType, result, paramTypes, args);
	}

	public void inputVariables(Class<?> clazz, String method, Type[] paramTypes, Object... args) {
		current().inputVariables(clazz, method, paramTypes, args);
	}

	public void outputVariables(Class<?> clazz, String method, Type[] paramTypes, Object... args) {
		current().outputVariables(clazz, method, paramTypes, args);
	}

	public void expectVariables(Object self, Object result, Object... args) {
		SnapshotProcess process = pop();
		process.expectVariables(self, result, args);
		consume(process.getSnapshot());
	}

	public void expectVariables(Object self, Object... args) {
		SnapshotProcess process = pop();
		process.expectVariables(self, args);
		consume(process.getSnapshot());
	}

	public void throwVariables(Object self, Throwable throwable, Object... args) {
		SnapshotProcess process = pop();
		process.throwVariables(self, throwable, args);
		consume(process.getSnapshot());
	}

	private void consume(ContextSnapshot snapshot) {
		if (snapshot.isValid()) {
			SnapshotConsumer snapshotConsumer = config.getSnapshotConsumer();
			if (snapshotConsumer != null) {
				snapshotConsumer.accept(snapshot);
			}
		}
	}

	private static Deque<SnapshotProcess> newStack() {
		if (currentThread().getThreadGroup() == RECORDING) {
			return new PassiveDeque<>(PASSIVE);
		} else {
			return new ArrayDeque<>();
		}
	}
}
