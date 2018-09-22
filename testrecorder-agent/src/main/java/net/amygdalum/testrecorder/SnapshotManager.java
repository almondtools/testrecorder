package net.amygdalum.testrecorder;

import static java.lang.System.identityHashCode;
import static java.lang.Thread.currentThread;
import static java.util.stream.Collectors.joining;
import static net.amygdalum.testrecorder.TestrecorderThreadFactory.RECORDING;
import static net.amygdalum.testrecorder.types.ContextSnapshot.INVALID;
import static net.amygdalum.testrecorder.util.Reflections.accessing;

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
import java.util.HashSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import net.amygdalum.testrecorder.bridge.BridgedSnapshotManager;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.profile.PerformanceProfile;
import net.amygdalum.testrecorder.serializers.SerializerFacade;
import net.amygdalum.testrecorder.types.ContextSnapshot;
import net.amygdalum.testrecorder.types.FieldSignature;
import net.amygdalum.testrecorder.types.MethodSignature;
import net.amygdalum.testrecorder.types.Profile;
import net.amygdalum.testrecorder.types.SerializationException;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.SerializedInput;
import net.amygdalum.testrecorder.types.SerializedInteraction;
import net.amygdalum.testrecorder.types.SerializedOutput;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.util.CircularityLock;
import net.amygdalum.testrecorder.util.Logger;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.bytebuddy.agent.ByteBuddyAgent;

public class SnapshotManager {

	private static final StackTraceValidator STACKTRACE_VALIDATOR = new StackTraceValidator()
		.invalidate(Logger.class);

	public static volatile SnapshotManager MANAGER;

	private SerializationThreadPoolExecutor snapshotExecutor;
	private CircularityLock lock = new CircularityLock();

	private MethodContext methodContext;
	private GlobalContext globalContext;

	private ThreadLocal<Deque<ContextSnapshot>> current;

	private SnapshotConsumer snapshotConsumer;
	private long timeoutInMillis;

	private ConfigurableSerializerFacade facade;

	static {
		Instrumentation inst = ByteBuddyAgent.install();
		installBridge(inst);
	}

	public SnapshotManager(AgentConfiguration config) {
		this.snapshotConsumer = config.loadConfiguration(SnapshotConsumer.class, config);

		this.current = ThreadLocal.withInitial(() -> newStack());
		this.facade = new ConfigurableSerializerFacade(config);

		PerformanceProfile performanceProfile = config.loadConfiguration(PerformanceProfile.class);

		this.timeoutInMillis = performanceProfile.getTimeoutInMillis();
		this.snapshotExecutor = new SerializationThreadPoolExecutor(performanceProfile.getIdleTime(), TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new TestrecorderThreadFactory("$snapshot"));
		this.methodContext = new MethodContext();
		this.globalContext = new GlobalContext();
	}

	private static void installBridge(Instrumentation inst) {
		try {
			inst.appendToBootstrapClassLoaderSearch(jarfile());
			BridgedSnapshotManager.inputVariables = MethodHandles.lookup().findVirtual(SnapshotManager.class, "inputVariables",
				MethodType.methodType(int.class, Object.class, String.class, Type.class, Type[].class));
			BridgedSnapshotManager.inputArguments = MethodHandles.lookup().findVirtual(SnapshotManager.class, "inputArguments",
				MethodType.methodType(void.class, int.class, Object[].class));
			BridgedSnapshotManager.inputResult = MethodHandles.lookup().findVirtual(SnapshotManager.class, "inputResult",
				MethodType.methodType(void.class, int.class, Object.class));
			BridgedSnapshotManager.inputVoidResult = MethodHandles.lookup().findVirtual(SnapshotManager.class, "inputVoidResult",
				MethodType.methodType(void.class, int.class));
			BridgedSnapshotManager.outputVariables = MethodHandles.lookup().findVirtual(SnapshotManager.class, "outputVariables",
				MethodType.methodType(int.class, Object.class, String.class, Type.class, Type[].class));
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

	public static SnapshotManager init(AgentConfiguration config) {
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

	public ContextSnapshotTransaction push(String signature) {
		ContextSnapshot snapshot = methodContext.createSnapshot(signature);
		current.get().push(snapshot);
		return new ValidContextSnapshotTransaction(snapshotExecutor, timeoutInMillis, facade, snapshot);
	}

	public ContextSnapshotTransaction pop(String signature) {
		Deque<ContextSnapshot> snapshots = current.get();
		while (!snapshots.isEmpty()) {
			ContextSnapshot snapshot = snapshots.pop();
			if (snapshot.matches(signature)) {
				return new ValidContextSnapshotTransaction(snapshotExecutor, timeoutInMillis, facade, snapshot);
			}
			snapshot.invalidate();
		}
		return DummyContextSnapshotTransaction.INVALID;
	}

	public ContextSnapshotTransaction current() {
		Deque<ContextSnapshot> stack = current.get();
		if (stack.isEmpty()) {
			return DummyContextSnapshotTransaction.INVALID;
		} else {
			ContextSnapshot snapshot = stack.peek();
			return new ValidContextSnapshotTransaction(snapshotExecutor, timeoutInMillis, facade, snapshot);
		}
	}

	public Queue<ContextSnapshot> all() {
		return current.get();
	}

	public Optional<ContextSnapshot> peek() {
		return Optional.ofNullable(current.get().peek());
	}

	public void setupVariables(Object self, String signature, Object... args) {
		try {
			boolean aquired = lock.acquire() && snapshotExecutor.submissionPermitted();
			if (!aquired || !matches(self, signature)) {
				return;
			}
			push(signature).to((facade, session, snapshot) -> {
				if (self != null) {
					snapshot.setSetupThis(facade.serialize(self.getClass(), self, session));
				}
				snapshot.setSetupArgs(facade.serialize(snapshot.getArgumentTypes(), args, session));
				snapshot.setSetupGlobals(globalContext.globals().stream()
					.map(field -> serializedGlobal(session, field))
					.toArray(SerializedField[]::new));
			});
		} finally {
			lock.release();
		}
	}

	public int inputVariables(Object object, String method, Type resultType, Type[] paramTypes) {
		try {
			boolean aquired = lock.acquire() && snapshotExecutor.submissionPermitted();
			if (!aquired || isNestedIO() || isInvalidStacktrace()) {
				return 0;
			}
			Class<?> clazz = toClass(object);
			int id = toId(object);

			MethodSignature signature = new MethodSignature(clazz, resultType, method, paramTypes);
			SerializedInput in = facade.serializeInput(id, signature);
			for (ContextSnapshot snapshot : all()) {
				snapshot.addInput(in);
			}
			return in.id();
		} finally {
			lock.release();
		}
	}

	public void inputArguments(int id, Object... arguments) {
		try {
			boolean aquired = lock.acquire() && snapshotExecutor.submissionPermitted();
			if (!aquired || id == 0) {
				return;
			}
			current().to((facade, session, snapshot) -> {
				snapshot.streamInput().filter(in -> in.id() == id).forEach(in -> {
					in.updateArguments(facade.serialize(in.getArgumentTypes(), arguments, session));
				});
			});
		} finally {
			lock.release();
		}
	}

	public void inputResult(int id, Object result) {
		try {
			boolean aquired = lock.acquire() && snapshotExecutor.submissionPermitted();
			if (!aquired || id == 0) {
				return;
			}
			current().to((facade, session, snapshot) -> {
				snapshot.streamInput().filter(in -> in.id() == id).forEach(in -> {
					in.updateResult(facade.serialize(in.getResultType(), result, session));
				});
			});
		} finally {
			lock.release();
		}
	}

	public void inputVoidResult(int id) {
		try {
			boolean aquired = lock.acquire() && snapshotExecutor.submissionPermitted();
			if (!aquired || id == 0) {
				return;
			}
			current().to((facade, session, snapshot) -> {
				snapshot.streamInput().filter(in -> in.id() == id).forEach(in -> {
					in.updateResult(SerializedNull.VOID);
				});
			});
		} finally {
			lock.release();
		}
	}

	public int outputVariables(Object object, String method, Type resultType, Type[] paramTypes) {
		try {
			boolean aquired = lock.acquire() && snapshotExecutor.submissionPermitted();
			if (!aquired || isNestedIO() || isInvalidStacktrace()) {
				return 0;
			}
			Class<?> clazz = toClass(object);
			int id = toId(object);

			MethodSignature signature = new MethodSignature(clazz, resultType, method, paramTypes);
			SerializedOutput out = facade.serializeOutput(id, signature);
			for (ContextSnapshot snapshot : all()) {
				snapshot.addOutput(out);
			}
			return out.id();
		} finally {
			lock.release();
		}
	}

	public void outputArguments(int id, Object... arguments) {
		try {
			boolean aquired = lock.acquire() && snapshotExecutor.submissionPermitted();
			if (!aquired || id == 0) {
				return;
			}
			current().to((facade, session, snapshot) -> {
				snapshot.streamOutput().filter(out -> out.id() == id).forEach(out -> {
					out.updateArguments(facade.serialize(out.getArgumentTypes(), arguments, session));
				});
			});
		} finally {
			lock.release();
		}
	}

	public void outputResult(int id, Object result) {
		try {
			boolean aquired = lock.acquire() && snapshotExecutor.submissionPermitted();
			if (!aquired || id == 0) {
				return;
			}
			current().to((facade, session, snapshot) -> {
				snapshot.streamOutput().filter(out -> out.id() == id).forEach(out -> {
					out.updateResult(facade.serialize(out.getResultType(), result, session));
				});
			});
		} finally {
			lock.release();
		}
	}

	public void outputVoidResult(int id) {
		try {
			boolean aquired = lock.acquire() && snapshotExecutor.submissionPermitted();
			if (!aquired || id == 0) {
				return;
			}
			current().to((facade, session, snapshot) -> {
				snapshot.streamOutput().filter(out -> out.id() == id).forEach(out -> {
					out.updateResult(SerializedNull.VOID);
				});
			});
		} finally {
			lock.release();
		}
	}

	private Class<?> toClass(Object object) {
		if (object instanceof Class<?>) {
			return (Class<?>) object;

		}
		return object.getClass();
	}

	private int toId(Object object) {
		return object instanceof Class<?> ? SerializedInteraction.STATIC : identityHashCode(object);
	}

	public void expectVariables(Object self, String signature, Object result, Object... args) {
		try {
			boolean aquired = lock.acquire() && snapshotExecutor.submissionPermitted();
			if (!aquired || !matches(self, signature)) {
				return;
			}
			pop(signature).to((facade, session, snapshot) -> {
				if (self != null) {
					snapshot.setExpectThis(facade.serialize(self.getClass(), self, session));
				}
				snapshot.setExpectResult(facade.serialize(snapshot.getResultType(), result, session));
				snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args, session));
				snapshot.setExpectGlobals(globalContext.globals().stream()
					.map(field -> serializedGlobal(session, field))
					.toArray(SerializedField[]::new));
			}).andConsume(this::consume);
		} finally {
			lock.release();
		}
	}

	public void expectVariables(Object self, String signature, Object... args) {
		try {
			boolean aquired = lock.acquire() && snapshotExecutor.submissionPermitted();
			if (!aquired || !matches(self, signature)) {
				return;
			}
			pop(signature).to((facade, session, snapshot) -> {
				if (self != null) {
					snapshot.setExpectThis(facade.serialize(self.getClass(), self, session));
				}
				snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args, session));
				snapshot.setExpectGlobals(globalContext.globals().stream()
					.map(field -> serializedGlobal(session, field))
					.toArray(SerializedField[]::new));
			}).andConsume(this::consume);
		} finally {
			lock.release();
		}
	}

	public void throwVariables(Throwable throwable, Object self, String signature, Object... args) {
		try {
			boolean aquired = lock.acquire() && snapshotExecutor.submissionPermitted();
			if (!aquired || !matches(self, signature)) {
				return;
			}
			pop(signature).to((facade, session, snapshot) -> {
				if (self != null) {
					snapshot.setExpectThis(facade.serialize(self.getClass(), self, session));
				}
				snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args, session));
				snapshot.setExpectException(facade.serialize(throwable.getClass(), throwable, session));
				snapshot.setExpectGlobals(globalContext.globals().stream()
					.map(field -> serializedGlobal(session, field))
					.toArray(SerializedField[]::new));
			}).andConsume(this::consume);
		} finally {
			lock.release();
		}
	}
	
	protected void consume(ContextSnapshot snapshot) {
		if (snapshot.isValid()) {
			if (snapshotConsumer != null) {
				snapshotConsumer.accept(snapshot);
			}
		}
	}

	public boolean isInvalidStacktrace() {
		for (StackTraceElement element : currentThread().getStackTrace()) {
			if (STACKTRACE_VALIDATOR.isInvalid(element)) {
				return true;
			}
		}
		return false;
	}

	public boolean isNestedIO() {
		return peek()
			.map(snapshot -> {
				boolean inputPending = snapshot.lastInputSatitisfies(in -> !in.isComplete());
				boolean outputPending = snapshot.lastOutputSatitisfies(out -> !out.isComplete());
				return inputPending || outputPending;
			})
			.orElse(false);
	}

	private static Deque<ContextSnapshot> newStack() {
		if (currentThread().getThreadGroup() == RECORDING) {
			return new PassiveDeque<>(INVALID);
		} else {
			return new ArrayDeque<>();
		}
	}

	private SerializedField serializedGlobal(SerializerSession session, Field field) {
		Class<?> declaringClass = field.getDeclaringClass();
		String name = field.getName();
		Class<?> type = field.getType();
		FieldSignature signature = new FieldSignature(declaringClass, type, name);
		Object value = globalOf(field);

		SerializedValue serializedValue = facade.serialize(type, value, session);
		return new SerializedField(signature, serializedValue);
	}

	private Object globalOf(Field field) {
		try {
			return accessing(field).call(f -> f.get(null));
		} catch (ReflectiveOperationException e) {
			throw new SerializationException(e);
		}
	}

	public interface ContextSnapshotTransaction {

		ContextSnapshotTransaction to(SerializationTask task);

		void andConsume(Consumer<ContextSnapshot> consumer);

	}

	public static class DummyContextSnapshotTransaction implements ContextSnapshotTransaction {

		public static final DummyContextSnapshotTransaction INVALID = new DummyContextSnapshotTransaction();

		@Override
		public ContextSnapshotTransaction to(SerializationTask task) {
			return this;
		}

		@Override
		public void andConsume(Consumer<ContextSnapshot> consumer) {
		}

	}

	public static class ValidContextSnapshotTransaction implements ContextSnapshotTransaction {

		private ExecutorService snapshotExecutor;
		private long timeoutInMillis;
		private SerializerFacade facade;

		private ContextSnapshot snapshot;

		public ValidContextSnapshotTransaction(ExecutorService snapshotExecutor, long timeoutInMillis, SerializerFacade facade, ContextSnapshot snapshot) {
			this.snapshotExecutor = snapshotExecutor;
			this.timeoutInMillis = timeoutInMillis;
			this.facade = facade;
			this.snapshot = snapshot;
		}

		@Override
		public ContextSnapshotTransaction to(SerializationTask task) {
			if (!snapshot.isValid()) {
				return this;
			}
			SerializerSession session = facade.newSession();
			try {
				Future<?> future = snapshotExecutor.submit(() -> {
					task.serialize(facade, session, snapshot);
				});
				future.get(timeoutInMillis, TimeUnit.MILLISECONDS);
				return this;
			} catch (InterruptedException | ExecutionException | TimeoutException | CancellationException e) {
				snapshot.invalidate();
				String profile = session.dumpProfiles().stream()
					.map(Profile::toString)
					.collect(joining("\n\t", "\n\t", ""));
				Logger.error("failed serializing " + snapshot + ", most time consuming types are:" + profile, e);
				return this;
			}
		}

		@Override
		public void andConsume(Consumer<ContextSnapshot> consumer) {
			consumer.accept(snapshot);
		}
	}

	public interface SerializationTask {
		void serialize(SerializerFacade facade, SerializerSession session, ContextSnapshot snapshot);
	}

	private static class StackTraceValidator {

		private Set<String> classNames;

		StackTraceValidator() {
			classNames = new HashSet<>();
		}

		public boolean isInvalid(StackTraceElement element) {
			return classNames.contains(element.getClassName());
		}

		public StackTraceValidator invalidate(Class<?> clazz) {
			classNames.add(clazz.getName());
			return this;
		}

	}
	
	private static class SerializationThreadPoolExecutor extends ThreadPoolExecutor {
		
		private Thread active;

		SerializationThreadPoolExecutor(long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
			super(0, 1, keepAliveTime, unit, workQueue, threadFactory);
		}
		
		@Override
		protected void beforeExecute(Thread t, Runnable r) {
			active = t;
		}

		public boolean submissionPermitted() {
			Thread current = currentThread();
			return current != active;
		}

	}
}
