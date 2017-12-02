package net.amygdalum.testrecorder;

import static java.lang.Thread.currentThread;
import static net.amygdalum.testrecorder.SnapshotProcess.PASSIVE;
import static net.amygdalum.testrecorder.TestrecorderThreadFactory.RECORDING;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.amygdalum.testrecorder.profile.SerializationProfile;

public class SnapshotManager {

	public static volatile SnapshotManager MANAGER;

	private ExecutorService snapshot;

	private Map<String, ContextSnapshotFactory> methodSnapshots;
	private GlobalContext globalContext;

	private ThreadLocal<Deque<SnapshotProcess>> current = ThreadLocal.withInitial(() -> newStack());

	private TestRecorderAgentConfig config;

	public SnapshotManager(TestRecorderAgentConfig config) {
		this.config = new FixedTestRecorderAgentConfig(config);

		this.snapshot = Executors.newSingleThreadExecutor(new TestrecorderThreadFactory("$snapshot"));
		this.methodSnapshots = new HashMap<>();
		this.globalContext = new GlobalContext();
	}

	public void close() throws Throwable {
		snapshot.shutdown();
		SnapshotConsumer snapshotConsumer = config.getSnapshotConsumer();
		if (snapshotConsumer != null) {
			snapshotConsumer.close();
		}
	}

	public static SnapshotManager init(TestRecorderAgentConfig config) {
		MANAGER = new SnapshotManager(config);
		return MANAGER;
	}

	public SnapshotConsumer getMethodConsumer() {
		return config.getSnapshotConsumer();
    }

	public void registerRecordedMethod(String signature, String className, String methodName, String methodDesc) {
		ContextSnapshotFactory factory = new ContextSnapshotFactory(config, signature, className, methodName, methodDesc);

		methodSnapshots.put(signature, factory);
	}

	public void registerGlobal(String className, String fieldName) {
		globalContext.add(className, fieldName);
	}

	private boolean matches(Object self, String signature) {
		if (self == null) {
			return true;
		}
		ContextSnapshotFactory contextSnapshotFactory = methodSnapshots.get(signature);
		return contextSnapshotFactory.signature().validIn(self.getClass());
	}

	public SnapshotProcess push(String signature) {
		ContextSnapshotFactory factory = methodSnapshots.get(signature);
		SerializationProfile profile = config;
		List<Field> contextGlobals = globalContext.globals();
		ContextSnapshot contextSnapshot = factory.createSnapshot();
		SnapshotProcess process = new SnapshotProcess(snapshot, profile, contextSnapshot, contextGlobals);
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

	public SnapshotProcess pop(String signature) {
		Deque<SnapshotProcess> processes = current.get();
		SnapshotProcess currentProcess = processes.pop();
		while (!currentProcess.matches(signature)) {
			currentProcess.getSnapshot().invalidate();
			currentProcess = processes.pop();
		}
		return currentProcess;
	}

	public void setupVariables(Object self, String signature, Object... args) {
		if (!matches(self, signature)) {
			return;
		}
		SnapshotProcess process = push(signature);
		process.setupVariables(signature, self, args);
	}

	public int inputVariables(StackTraceElement[] stackTrace, Object object, String method, Type resultType, Type[] paramTypes) {
		return current().inputVariables(stackTrace, object, method, resultType, paramTypes);
	}

	public void inputArguments(int id, Object... args) {
		current().inputArguments(id, args);
	}

	public void inputResult(int id, Object result) {
		current().inputResult(id, result);
	}

	public int outputVariables(StackTraceElement[] stackTrace, Object object, String method, Type resultType, Type[] paramTypes) {
		return current().outputVariables(stackTrace, object, method, resultType, paramTypes);
	}

	public void outputArguments(int id, Object... args) {
		current().outputArguments(id, args);
	}

	public void outputResult(int id, Object result) {
		current().outputResult(id, result);
	}

	public void expectVariables(Object self, String signature, Object result, Object... args) {
		if (!matches(self, signature)) {
			return;
		}
		SnapshotProcess process = pop(signature);
		process.expectVariables(self, result, args);
		consume(process.getSnapshot());
	}

	public void expectVariables(Object self, String signature, Object... args) {
		if (!matches(self, signature)) {
			return;
		}
		SnapshotProcess process = pop(signature);
		process.expectVariables(self, args);
		consume(process.getSnapshot());
	}

	public void throwVariables(Throwable throwable, Object self, String signature, Object... args) {
		if (!matches(self, signature)) {
			return;
		}
		SnapshotProcess process = pop(signature);
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
