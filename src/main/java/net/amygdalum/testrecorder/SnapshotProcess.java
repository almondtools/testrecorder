package net.amygdalum.testrecorder;

import static java.lang.System.identityHashCode;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import net.amygdalum.testrecorder.profile.SerializationProfile;
import net.amygdalum.testrecorder.serializers.SerializerFacade;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedOutput;

public class SnapshotProcess {

	private static final StackTraceElement[] UNRESOLVED = new StackTraceElement[] { new StackTraceElement("?", "?", "?", 0) };

	public static final SnapshotProcess PASSIVE = passiveProcess();

	private ExecutorService executor;
	private long timeoutInMillis;
	private ContextSnapshot snapshot;
	private SerializerFacade facade;
	private List<Field> globals;
	private Deque<SerializedInput> input;
	private Deque<SerializedOutput> output;

	private SnapshotProcess() {
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

	private StackTraceElement[] call(StackTraceElement[] stackTrace, String methodName) {
		for (int i = 0; i < stackTrace.length; i++) {
			StackTraceElement caller = stackTrace[i];
			if (methodName.equals(caller.getMethodName())) {
				return Arrays.copyOfRange(stackTrace, i, stackTrace.length);
			}
		}
		return UNRESOLVED;
	}

	public int inputVariables(StackTraceElement[] stackTrace, Object object, String method, Type resultType, Type[] paramTypes) {
		if (isNestedIO(stackTrace, method)) {
			return 0;
		}
		StackTraceElement[] call = call(stackTrace, method);
		Class<?> clazz = object instanceof Class<?> ? (Class<?>) object : object.getClass();
		int id = object instanceof Class<?> ? 0 : identityHashCode(object);

		SerializedInput in = new SerializedInput(id, call, clazz, method, resultType, paramTypes);
		input.add(in);
		return in.id();
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

	public int outputVariables(StackTraceElement[] stackTrace, Object object, String method, Type resultType, Type[] paramTypes) {
		if (isNestedIO(stackTrace, method)) {
			return 0;
		}
		StackTraceElement[] call = call(stackTrace, method);
		Class<?> clazz = object instanceof Class<?> ? (Class<?>) object : object.getClass();
		int id = object instanceof Class<?> ? 0 : identityHashCode(object);

		SerializedOutput out = new SerializedOutput(id, call, clazz, method, resultType, paramTypes);
		output.add(out);
		return out.id();
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
			System.err.println("failed serializing " + snapshot);
			e.printStackTrace(System.err);
		}
	}

	private static SnapshotProcess passiveProcess() {
		return new SnapshotProcess() {

			@Override
			public int inputVariables(StackTraceElement[] stackTrace, Object object, String method, Type resultType, Type[] paramTypes) {
				return 0;
			}

			@Override
			public void inputArguments(int id, Object... arguments) {
			}

			@Override
			public void inputResult(int id, Object result) {
			}

			@Override
			public int outputVariables(StackTraceElement[] stackTrace, Object object, String method, Type resultType, Type[] paramTypes) {
				return 0;
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

}
