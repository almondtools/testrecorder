package net.amygdalum.testrecorder;

import static java.lang.Thread.currentThread;
import static net.amygdalum.testrecorder.SnapshotProcess.PASSIVE;
import static net.amygdalum.testrecorder.SnapshotProcess.input;
import static net.amygdalum.testrecorder.TestrecorderThreadFactory.RECORDING;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import net.amygdalum.testrecorder.bridge.BridgedSnapshotManager;
import net.amygdalum.testrecorder.profile.SerializationProfile;
import net.amygdalum.testrecorder.runtime.FakeIO;
import net.bytebuddy.agent.ByteBuddyAgent;

public class SnapshotManager {

	public static volatile SnapshotManager MANAGER;

	private ExecutorService snapshot;

	private Map<String, ContextSnapshotFactory> methodSnapshots;
	private GlobalContext globalContext;

	private ThreadLocal<Deque<SnapshotProcess>> current = ThreadLocal.withInitial(() -> newStack());

	private TestRecorderAgentConfig config;

	static {
		Instrumentation inst = ByteBuddyAgent.install();
		installBridge(inst);
	}
	
	public SnapshotManager(TestRecorderAgentConfig config) {
		this.config = new FixedTestRecorderAgentConfig(config);

		this.snapshot = Executors.newSingleThreadExecutor(new TestrecorderThreadFactory("$snapshot"));
		this.methodSnapshots = new HashMap<>();
		this.globalContext = new GlobalContext();
	}

	private static void installBridge(Instrumentation inst) {
		try {
			inst.appendToBootstrapClassLoaderSearch(jarfile());
			BridgedSnapshotManager.inputVariables = MethodHandles.lookup().findVirtual(SnapshotManager.class, "inputVariables",
				MethodType.methodType(int.class, StackTraceElement[].class, Object.class, String.class, Type.class, Type[].class));
			BridgedSnapshotManager.inputArguments = MethodHandles.lookup().findVirtual(SnapshotManager.class, "inputArguments",
				MethodType.methodType(void.class, int.class, Object[].class));
			BridgedSnapshotManager.inputResult = MethodHandles.lookup().findVirtual(SnapshotManager.class, "inputResult",
				MethodType.methodType(void.class, int.class, Object.class));
			BridgedSnapshotManager.outputVariables = MethodHandles.lookup().findVirtual(SnapshotManager.class, "outputVariables",
				MethodType.methodType(int.class, StackTraceElement[].class, Object.class, String.class, Type.class, Type[].class));
			BridgedSnapshotManager.outputArguments = MethodHandles.lookup().findVirtual(SnapshotManager.class, "outputArguments",
				MethodType.methodType(void.class, int.class, Object[].class));
			BridgedSnapshotManager.outputResult = MethodHandles.lookup().findVirtual(SnapshotManager.class, "outputResult",
				MethodType.methodType(void.class, int.class, Object.class));
		} catch (ReflectiveOperationException | IOException e) {
			throw new RuntimeException("failed installing fake bridge", e);
		}
	}
	
	private static JarFile jarfile() throws IOException {
		String bridge = "net/amygdalum/testrecorder/bridge/BridgedSnapshotManager.class";
		InputStream resourceStream = FakeIO.class.getResourceAsStream("/" + bridge);
		if (resourceStream == null) {
			throw new FileNotFoundException(bridge);
		}
		try (InputStream inputStream = resourceStream) {
			File agentJar = File.createTempFile("agent", "jar");
			agentJar.deleteOnExit();
			Manifest manifest = new Manifest();
			try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(agentJar), manifest)) {

				jarOutputStream.putNextEntry(new JarEntry(bridge));
				byte[] buffer = new byte[4096];
				int index;
				while ((index = inputStream.read(buffer)) != -1) {
					jarOutputStream.write(buffer, 0, index);
				}
				jarOutputStream.closeEntry();
			}
			return new JarFile(agentJar);
		}
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
		BridgedSnapshotManager.MANAGER = MANAGER;
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

	public Queue<SnapshotProcess> all() {
		return current.get();
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
		return input(all()).variables(stackTrace, object, method, resultType, paramTypes);
	}

	public void inputArguments(int id, Object... args) {
		current().inputArguments(id, args);
	}

	public void inputResult(int id, Object result) {
		current().inputResult(id, result);
	}

	public int outputVariables(StackTraceElement[] stackTrace, Object object, String method, Type resultType, Type[] paramTypes) {
		return SnapshotProcess.output(all()).variables(stackTrace, object, method, resultType, paramTypes);
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
