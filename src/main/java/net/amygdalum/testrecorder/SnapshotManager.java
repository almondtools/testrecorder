package net.amygdalum.testrecorder;

import static java.lang.System.identityHashCode;
import static java.lang.Thread.currentThread;
import static net.amygdalum.testrecorder.ContextSnapshot.INVALID;
import static net.amygdalum.testrecorder.Recorder.isRecording;
import static net.amygdalum.testrecorder.TestrecorderThreadFactory.RECORDING;
import static net.amygdalum.testrecorder.types.SerializedInteraction.VOID;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import net.amygdalum.testrecorder.bridge.BridgedSnapshotManager;
import net.amygdalum.testrecorder.runtime.FakeIO;
import net.amygdalum.testrecorder.serializers.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedOutput;
import net.bytebuddy.agent.ByteBuddyAgent;

public class SnapshotManager {

	public static volatile SnapshotManager MANAGER;

	private ExecutorService snapshotExecutor;

	private MethodContext methodContext;
	private GlobalContext globalContext;

	private ThreadLocal<Deque<ContextSnapshot>> current;

	private SnapshotConsumer snapshotConsumer;
	private long timeoutInMillis;

	private ThreadLocal<ConfigurableSerializerFacade> facade;

	static {
		Instrumentation inst = ByteBuddyAgent.install();
		installBridge(inst);
	}

	public SnapshotManager(TestRecorderAgentConfig config) {
		this.snapshotConsumer = config.getSnapshotConsumer();
		this.timeoutInMillis = config.getTimeoutInMillis();
		this.current = ThreadLocal.withInitial(() -> newStack());
		this.facade = ThreadLocal.withInitial(() -> new ConfigurableSerializerFacade(config));

		this.snapshotExecutor = Executors.newSingleThreadExecutor(new TestrecorderThreadFactory("$snapshot"));
		this.methodContext = new MethodContext();
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
			BridgedSnapshotManager.inputVoidResult = MethodHandles.lookup().findVirtual(SnapshotManager.class, "inputVoidResult",
				MethodType.methodType(void.class, int.class));
			BridgedSnapshotManager.outputVariables = MethodHandles.lookup().findVirtual(SnapshotManager.class, "outputVariables",
				MethodType.methodType(int.class, StackTraceElement[].class, Object.class, String.class, Type.class, Type[].class));
			BridgedSnapshotManager.outputArguments = MethodHandles.lookup().findVirtual(SnapshotManager.class, "outputArguments",
				MethodType.methodType(void.class, int.class, Object[].class));
			BridgedSnapshotManager.outputResult = MethodHandles.lookup().findVirtual(SnapshotManager.class, "outputResult",
				MethodType.methodType(void.class, int.class, Object.class));
			BridgedSnapshotManager.outputVoidResult = MethodHandles.lookup().findVirtual(SnapshotManager.class, "outputVoidResult",
				MethodType.methodType(void.class, int.class));
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
		snapshotExecutor.shutdown();
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
		return snapshotConsumer;
	}

	public void registerRecordedMethod(String signature, String className, String methodName, String methodDesc) {
		methodContext.add(signature, className, methodName, methodDesc);
	}

	public void registerGlobal(String className, String fieldName) {
		globalContext.add(className, fieldName);
	}

	private boolean matches(Object self, String signature) {
		if (self == null) {
			return true;
		}
		return methodContext.signature(signature).validIn(self.getClass());
	}

	public ContextSnapshot push(String signature) {
		ContextSnapshot snapshot = methodContext.createSnapshot(signature);
		current.get().push(snapshot);
		return snapshot;
	}

	public ContextSnapshot current() {
		Deque<ContextSnapshot> stack = current.get();
		if (stack.isEmpty()) {
			return ContextSnapshot.INVALID;
		} else {
			return stack.peek();
		}
	}

	public Queue<ContextSnapshot> all() {
		return current.get();
	}

	public ContextSnapshot pop(String signature) {
		Deque<ContextSnapshot> processes = current.get();
		ContextSnapshot currentProcess = processes.pop();
		while (!currentProcess.matches(signature)) {
			currentProcess.invalidate();
			currentProcess = processes.pop();
		}
		return currentProcess;
	}

	public void setupVariables(Object self, String signature, Object... args) {
		if (!matches(self, signature)) {
			return;
		}
		execute((facade, snapshot) -> {
			if (self != null) {
				snapshot.setSetupThis(facade.serialize(self.getClass(), self));
			}
			snapshot.setSetupArgs(facade.serialize(snapshot.getArgumentTypes(), args));
			snapshot.setSetupGlobals(globalContext.globals().stream()
				.map(field -> facade.serialize(field, null))
				.toArray(SerializedField[]::new));
		}, push(signature));
	}

	public int inputVariables(StackTraceElement[] stackTrace, Object object, String method, Type resultType, Type[] paramTypes) {
		if (isRecording(stackTrace) || isNestedIO()) {
			return 0;
		}
		Class<?> clazz = object instanceof Class<?> ? (Class<?>) object : object.getClass();
		int id = object instanceof Class<?> ? 0 : identityHashCode(object);

		SerializedInput in = new SerializedInput(id, clazz, method, resultType, paramTypes);
		for (ContextSnapshot snapshot : all()) {
			snapshot.addInput(in);
		}
		return in.id();
	}

	public void inputArguments(int id, Object... arguments) {
		ContextSnapshot currentSnapshot = current();
		execute((facade, snapshot) -> {
			snapshot.streamInput().filter(in -> in.id() == id).forEach(in -> {
				in.updateArguments(facade.serialize(in.getTypes(), arguments));
			});
		}, currentSnapshot);
	}

	public void inputResult(int id, Object result) {
		ContextSnapshot currentSnapshot = current();
		execute((facade, snapshot) -> {
			snapshot.streamInput().filter(in -> in.id() == id).forEach(in -> {
				in.updateResult(facade.serialize(in.getResultType(), result));
			});
		}, currentSnapshot);
	}

	public void inputVoidResult(int id) {
		ContextSnapshot currentSnapshot = current();
		execute((facade, snapshot) -> {
			snapshot.streamInput().filter(in -> in.id() == id).forEach(in -> {
				in.updateResult(VOID);
			});
		}, currentSnapshot);
	}
	
	public int outputVariables(StackTraceElement[] stackTrace, Object object, String method, Type resultType, Type[] paramTypes) {
		if (isRecording(stackTrace) || isNestedIO()) {
			return 0;
		}
		Class<?> clazz = object instanceof Class<?> ? (Class<?>) object : object.getClass();
		int id = object instanceof Class<?> ? 0 : identityHashCode(object);

		SerializedOutput out = new SerializedOutput(id, clazz, method, resultType, paramTypes);
		for (ContextSnapshot snapshot : all()) {
			snapshot.addOutput(out);
		}
		return out.id();
	}

	public void outputArguments(int id, Object... arguments) {
		ContextSnapshot currentSnapshot = current();
		execute((facade, snapshot) -> {
			snapshot.streamOutput().filter(out -> out.id() == id).forEach(out -> {
				out.updateArguments(facade.serialize(out.getTypes(), arguments));
			});
		}, currentSnapshot);
	}

	public void outputResult(int id, Object result) {
		ContextSnapshot currentSnapshot = current();
		execute((facade, snapshot) -> {
			snapshot.streamOutput().filter(out -> out.id() == id).forEach(out -> {
				out.updateResult(facade.serialize(out.getResultType(), result));
			});
		}, currentSnapshot);
	}

	public void outputVoidResult(int id) {
		ContextSnapshot currentSnapshot = current();
		execute((facade, snapshot) -> {
			snapshot.streamOutput().filter(out -> out.id() == id).forEach(out -> {
				out.updateResult(VOID);
			});
		}, currentSnapshot);
	}

	public void expectVariables(Object self, String signature, Object result, Object... args) {
		if (!matches(self, signature)) {
			return;
		}
		ContextSnapshot currentSnapshot = pop(signature);
		execute((facade, snapshot) -> {
			if (self != null) {
				snapshot.setExpectThis(facade.serialize(self.getClass(), self));
			}
			snapshot.setExpectResult(facade.serialize(snapshot.getResultType(), result));
			snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
			snapshot.setExpectGlobals(globalContext.globals().stream()
				.map(field -> facade.serialize(field, null))
				.toArray(SerializedField[]::new));
		}, currentSnapshot);
		consume(currentSnapshot);
	}

	public void expectVariables(Object self, String signature, Object... args) {
		if (!matches(self, signature)) {
			return;
		}
		ContextSnapshot currentSnapshot = pop(signature);
		execute((facade, snapshot) -> {
			if (self != null) {
				snapshot.setExpectThis(facade.serialize(self.getClass(), self));
			}
			snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
			snapshot.setExpectGlobals(globalContext.globals().stream()
				.map(field -> facade.serialize(field, null))
				.toArray(SerializedField[]::new));
		}, currentSnapshot);
		consume(currentSnapshot);
	}

	public void throwVariables(Throwable throwable, Object self, String signature, Object... args) {
		if (!matches(self, signature)) {
			return;
		}
		ContextSnapshot currentSnapshot = pop(signature);
		execute((facade, snapshot) -> {
			if (self != null) {
				snapshot.setExpectThis(facade.serialize(self.getClass(), self));
			}
			snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
			snapshot.setExpectException(facade.serialize(throwable.getClass(), throwable));
			snapshot.setExpectGlobals(globalContext.globals().stream()
				.map(field -> facade.serialize(field, null))
				.toArray(SerializedField[]::new));
		}, currentSnapshot);
		consume(currentSnapshot);
	}

	public boolean isNestedIO() {
		ContextSnapshot snapshot = current();
		boolean inputPending = snapshot.lastInputSatitisfies(in -> !in.isComplete());
		boolean outputPending = snapshot.lastOutputSatitisfies(out -> !out.isComplete());
		return inputPending || outputPending;
	}

	private void consume(ContextSnapshot snapshot) {
		if (snapshot.isValid()) {
			if (snapshotConsumer != null) {
				snapshotConsumer.accept(snapshot);
			}
		}
	}

	private static Deque<ContextSnapshot> newStack() {
		if (currentThread().getThreadGroup() == RECORDING) {
			return new PassiveDeque<>(INVALID);
		} else {
			return new ArrayDeque<>();
		}
	}

	private void execute(SerializationTask task, ContextSnapshot snapshot) {
		try {
			ConfigurableSerializerFacade currentFacade = facade.get();
			Future<?> future = snapshotExecutor.submit(() -> {
				task.serialize(currentFacade, snapshot);
			});
			future.get(timeoutInMillis, TimeUnit.MILLISECONDS);
			currentFacade.reset();
		} catch (InterruptedException | ExecutionException | TimeoutException | CancellationException e) {
			snapshot.invalidate();
			Logger.error("failed serializing " + snapshot, e);
		}
	}

	interface SerializationTask {
		void serialize(SerializerFacade facade, ContextSnapshot snapshot);
	}

}
