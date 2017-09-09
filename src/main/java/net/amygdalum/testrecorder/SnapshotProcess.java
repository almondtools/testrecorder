package net.amygdalum.testrecorder;

import static java.lang.System.identityHashCode;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

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
	private List<SerializedInput> input;
	private List<SerializedOutput> output;

	private SnapshotProcess() {
	}

	public SnapshotProcess(ExecutorService executor, long timeoutInMillis, ContextSnapshotFactory factory, List<Field> globals) {
		this.executor = executor;
		this.timeoutInMillis = timeoutInMillis;
		this.snapshot = factory.createSnapshot();
		this.facade = new ConfigurableSerializerFacade(factory.profile());
		this.globals = globals;
		this.input = new ArrayList<>();
		this.output = new ArrayList<>();
	}

	public ContextSnapshot getSnapshot() {
		return snapshot;
	}

	public void inputVariables(Object object, String method, Type resultType, Object result, Type[] paramTypes, Object[] args) {
		Class<?> clazz = object instanceof Class<?> ? (Class<?>) object : object.getClass();
		int id = object instanceof Class<?> ? 0 : identityHashCode(object);
		input.add(new SerializedInput(id, clazz, method, resultType, facade.serialize(resultType, result), paramTypes, facade.serialize(paramTypes, args)));
	}

	public void inputVariables(Object object, String method, Type[] paramTypes, Object[] args) {
		Class<?> clazz = object instanceof Class<?> ? (Class<?>) object : object.getClass();
		int id = object instanceof Class<?> ? 0 : identityHashCode(object);
		input.add(new SerializedInput(id, clazz, method, paramTypes, facade.serialize(paramTypes, args)));
	}

	public void outputVariables(Object object, String method, Type[] paramTypes, Object[] args) {
		Class<?> clazz = object instanceof Class<?> ? (Class<?>) object : object.getClass();
		int id = object instanceof Class<?> ? 0 : identityHashCode(object);
		output.add(new SerializedOutput(id, clazz, method, paramTypes, facade.serialize(paramTypes, args)));
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
			snapshot.setSetupInput(input);
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
			snapshot.setExpectOutput(output);
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
			snapshot.setExpectOutput(output);
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
			snapshot.setExpectOutput(output);
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
			System.out.println("failed serializing " + snapshot);
		}
	}

	private static SnapshotProcess passiveProcess() {
		return new SnapshotProcess() {

			@Override
			public void inputVariables(Object object, String method, Type resultType, Object result, Type[] paramTypes, Object[] args) {
			}

			@Override
			public void inputVariables(Object object, String method, Type[] paramTypes, Object[] args) {
			}

			@Override
			public void outputVariables(Object object, String method, Type[] paramTypes, Object[] args) {
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
