package net.amygdalum.testrecorder;

import static java.lang.System.identityHashCode;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import net.amygdalum.testrecorder.profile.SerializationProfile;
import net.amygdalum.testrecorder.serializers.SerializerFacade;
import net.amygdalum.testrecorder.util.Types;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedOutput;

public class SnapshotProcess {

	public static final SnapshotProcess PASSIVE = passiveProcess();

	private ExecutorService executor;
	private long timeoutInMillis;
	private ContextSnapshot snapshot;
	private SerializerFacade facade;
	private List<Field> globals;
	private Deque<SerializedInput> input;
	private Deque<SerializedOutput> output;

	private SnapshotProcess() {
		this.input = new LinkedList<>();
		this.output = new LinkedList<>();
	}

	public SnapshotProcess(ExecutorService executor, SerializationProfile profile, ContextSnapshot snapshot, List<Field> globals) {
		this.executor = executor;
		this.timeoutInMillis = profile.getTimeoutInMillis();
		this.snapshot = snapshot;
		this.facade = new ConfigurableSerializerFacade(profile);
		this.globals = globals;
		this.input = new LinkedList<>();
		this.output = new LinkedList<>();
	}

	public ContextSnapshot getSnapshot() {
		return snapshot;
	}

	public boolean matches(String key) {
		return snapshot.matches(key);
	}

	private static StackTraceElement[] call(StackTraceElement[] stackTrace, Class<?> clazz, String methodName) {
		for (int i = 0; i < stackTrace.length; i++) {
			StackTraceElement caller = stackTrace[i];
			if (matches(clazz, methodName, caller)) {
				return Arrays.copyOfRange(stackTrace, i, stackTrace.length);
			}
		}
		StackTraceElement[] call = new StackTraceElement[stackTrace.length + 1];
		System.arraycopy(stackTrace, 0, call, 1, stackTrace.length);
		call[0] = new StackTraceElement(clazz.getName(), methodName, "?", -1);
		return call;
	}

	private static boolean matches(Class<?> clazz, String methodName, StackTraceElement caller) {
		if (!caller.getMethodName().equals(methodName)) {
			return false;
		}
		List<Method> qualifyingMethods = Types.getDeclaredMethods(clazz, methodName);
		return qualifyingMethods.stream().anyMatch(method -> method.getName().equals(caller.getMethodName()) && method.getDeclaringClass().getName().equals(caller.getClassName()));
	}

	public void inputResult(int id, Object result) {
		input.stream().filter(in -> in.id() == id).forEach(in -> {
			in.updateResult(facade.serialize(in.getResultType(), result));
		});
	}

	public void inputArguments(int id, Object... arguments) {
		input.stream().filter(in -> in.id() == id).forEach(in -> {
			in.updateArguments(facade.serialize(in.getTypes(), arguments));
		});
	}

	public void outputResult(int id, Object result) {
		output.stream().filter(out -> out.id() == id).forEach(out -> {
			out.updateResult(facade.serialize(out.getResultType(), result));
		});
	}

	public void outputArguments(int id, Object... arguments) {
		output.stream().filter(out -> out.id() == id).forEach(out -> {
			out.updateArguments(facade.serialize(out.getTypes(), arguments));
		});
	}

	private boolean isNestedIO(StackTraceElement[] stackTrace, String methodName) {
		if (!input.isEmpty()) {
			SerializedInput in = input.getLast();
			if (in.prefixesStackTrace(stackTrace)) {
				return true;
			}
		}
		if (!output.isEmpty()) {
			SerializedOutput out = output.getLast();
			if (out.prefixesStackTrace(stackTrace)) {
				return true;
			}
		}
		return false;
	}

	public void setupVariables(String signature, Object self, Object... args) {
		modify(snapshot -> {
			if (self != null) {
				snapshot.setSetupThis(facade.serialize(self.getClass(), self));
			}
			snapshot.setSetupArgs(facade.serialize(snapshot.getArgumentTypes(), args));
			snapshot.setSetupGlobals(globals.stream()
				.map(field -> facade.serialize(field, null))
				.toArray(SerializedField[]::new));
		});
	}

	public void expectVariables(Object self, Object result, Object... args) {
		modify(snapshot -> {
			if (self != null) {
				snapshot.setExpectThis(facade.serialize(self.getClass(), self));
			}
			snapshot.setExpectResult(facade.serialize(snapshot.getResultType(), result));
			snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
			snapshot.setExpectGlobals(globals.stream()
				.map(field -> facade.serialize(field, null))
				.toArray(SerializedField[]::new));
			snapshot.setInput(new ArrayList<>(input));
			snapshot.setOutput(new ArrayList<>(output));
		});
	}

	public void expectVariables(Object self, Object... args) {
		modify(snapshot -> {
			if (self != null) {
				snapshot.setExpectThis(facade.serialize(self.getClass(), self));
			}
			snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
			snapshot.setExpectGlobals(globals.stream()
				.map(field -> facade.serialize(field, null))
				.toArray(SerializedField[]::new));
			snapshot.setInput(new ArrayList<>(input));
			snapshot.setOutput(new ArrayList<>(output));
		});
	}

	public void throwVariables(Object self, Throwable throwable, Object[] args) {
		modify(snapshot -> {
			if (self != null) {
				snapshot.setExpectThis(facade.serialize(self.getClass(), self));
			}
			snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
			snapshot.setExpectException(facade.serialize(throwable.getClass(), throwable));
			snapshot.setExpectGlobals(globals.stream()
				.map(field -> facade.serialize(field, null))
				.toArray(SerializedField[]::new));
			snapshot.setInput(new ArrayList<>(input));
			snapshot.setOutput(new ArrayList<>(output));
		});
	}

	private void modify(Consumer<ContextSnapshot> task) {
		try {
			Future<?> future = executor.submit(() -> {
				task.accept(snapshot);
			});
			future.get(timeoutInMillis, TimeUnit.MILLISECONDS);
			facade.reset();
		} catch (InterruptedException | ExecutionException | TimeoutException | CancellationException e) {
			snapshot.invalidate();
			Logger.error("failed serializing " + snapshot, e);
		}
	}

	public static In input(Queue<SnapshotProcess> processes) {
		return (stackTrace, object, method, resultType, paramTypes) -> {
			if (processes.isEmpty() || processes.peek().isNestedIO(stackTrace, method)) {
				return 0;
			}
			Class<?> clazz = object instanceof Class<?> ? (Class<?>) object : object.getClass();
			StackTraceElement[] call = call(stackTrace, clazz, method);
			int id = object instanceof Class<?> ? 0 : identityHashCode(object);

			SerializedInput in = new SerializedInput(id, call, clazz, method, resultType, paramTypes);
			for (SnapshotProcess process : processes) {
				process.input.add(in);
			}
			return in.id();
		};
	}

	public static Out output(Queue<SnapshotProcess> processes) {
		return (stackTrace, object, method, resultType, paramTypes) -> {
			if (processes.isEmpty() || processes.peek().isNestedIO(stackTrace, method)) {
				return 0;
			}
			Class<?> clazz = object instanceof Class<?> ? (Class<?>) object : object.getClass();
			StackTraceElement[] call = call(stackTrace, clazz, method);
			int id = object instanceof Class<?> ? 0 : identityHashCode(object);

			SerializedOutput out = new SerializedOutput(id, call, clazz, method, resultType, paramTypes);
			for (SnapshotProcess process : processes) {
				process.output.add(out);
			}
			return out.id();
		};
	}

	private static SnapshotProcess passiveProcess() {
		return new SnapshotProcess() {

			@Override
			public void inputArguments(int id, Object... arguments) {
			}

			@Override
			public void inputResult(int id, Object result) {
			}

			@Override
			public void outputArguments(int id, Object... arguments) {
			}

			@Override
			public void outputResult(int id, Object result) {
			}

			@Override
			public void setupVariables(String signature, Object self, Object... args) {
			}

			@Override
			public void expectVariables(Object self, Object result, Object... args) {
			}

			@Override
			public void expectVariables(Object self, Object... args) {
			}

			@Override
			public void throwVariables(Object self, Throwable throwable, Object[] args) {
			}

			@Override
			public ContextSnapshot getSnapshot() {
				return ContextSnapshot.INVALID;
			}
		};
	}
	
	interface In {
		int variables(StackTraceElement[] stackTrace, Object object, String method, Type resultType, Type[] paramTypes);
	}

	interface Out {
		int variables(StackTraceElement[] stackTrace, Object object, String method, Type resultType, Type[] paramTypes);
	}

}
