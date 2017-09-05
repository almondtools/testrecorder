package net.amygdalum.testrecorder;

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

	private Integer prepareId;

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

	public void prepareInputOutput(int id) {
		if (this.prepareId == null) {
			this.prepareId = id;
		}
	}

	public void inputVariables(int id, Class<?> clazz, String method, Type resultType, Object result, Type[] paramTypes, Object[] args) {
		if (this.prepareId == id) {
			input.add(new SerializedInput(clazz, method, resultType, facade.serialize(resultType, result), paramTypes, facade.serialize(paramTypes, args)));
			this.prepareId = null;
		}
	}

	public void inputVariables(int id, Class<?> clazz, String method, Type[] paramTypes, Object[] args) {
		if (this.prepareId == id) {
			input.add(new SerializedInput(clazz, method, paramTypes, facade.serialize(paramTypes, args)));
			this.prepareId = null;
		}
	}

	public void outputVariables(int id, Class<?> clazz, String method, Type[] paramTypes, Object[] args) {
		if (this.prepareId == id) {
			output.add(new SerializedOutput(clazz, method, paramTypes, facade.serialize(paramTypes, args)));
			this.prepareId = null;
		}
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
			public void prepareInputOutput(int id) {
			}
			
			@Override
			public void inputVariables(int id, Class<?> clazz, String method, Type resultType, Object result, Type[] paramTypes, Object[] args) {
			}

			@Override
			public void inputVariables(int id, Class<?> clazz, String method, Type[] paramTypes, Object[] args) {
			}

			@Override
			public void outputVariables(int id, Class<?> clazz, String method, Type[] paramTypes, Object[] args) {
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
