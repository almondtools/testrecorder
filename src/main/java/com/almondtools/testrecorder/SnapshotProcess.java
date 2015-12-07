package com.almondtools.testrecorder;

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
import java.util.stream.Stream;

import com.almondtools.testrecorder.values.SerializedField;
import com.almondtools.testrecorder.values.SerializedOutput;

public class SnapshotProcess {

	private ExecutorService executor;
	private long timeoutInMillis;
	private ContextSnapshot snapshot;
	private SerializerFacade facade;
	private List<Field> globals;
	private List<SerializedOutput> output;

	public SnapshotProcess(ExecutorService executor, long timeoutInMillis, ContextSnapshotFactory factory) {
		this.executor = executor;
		this.timeoutInMillis = timeoutInMillis;
		this.snapshot = factory.createSnapshot();
		this.facade = new ConfigurableSerializerFacade(factory.profile());
		this.globals = factory.getGlobalFields();
		this.output = new ArrayList<>();
	}
	
	public ContextSnapshot getSnapshot() {
		return snapshot;
	}
	
	public void outputVariables(Class<?> clazz, String method, Object[] args) {
		Type[] classes = Stream.of(args)
			.map(Object::getClass)
			.toArray(Type[]::new);
		output.add(new SerializedOutput(clazz, method, facade.serialize(classes, args)));
	}

	public void setupVariables(String signature, Object self, Object... args) {
		modify(snapshot -> {
			snapshot.setSetupThis(facade.serialize(self.getClass(), self));
			snapshot.setSetupArgs(facade.serialize(snapshot.getArgumentTypes(), args));
			snapshot.setSetupGlobals(globals.stream()
				.map(field -> facade.serialize(field, null))
				.toArray(SerializedField[]::new));
		});
	}

	public void expectVariables(Object self, Object result, Object... args) {
		modify(snapshot -> {
			snapshot.setExpectThis(facade.serialize(self.getClass(), self));
			snapshot.setExpectResult(facade.serialize(snapshot.getResultType(), result));
			snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
			snapshot.setExpectGlobals(globals.stream()
				.map(field -> facade.serialize(field, null))
				.toArray(SerializedField[]::new));
		});
	}

	public void expectVariables(Object self, Object... args) {
		modify(snapshot -> {
			snapshot.setExpectThis(facade.serialize(self.getClass(), self));
			snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
			snapshot.setExpectGlobals(globals.stream()
				.map(field -> facade.serialize(field, null))
				.toArray(SerializedField[]::new));
		});
	}
	
	public void throwVariables(Object self, Throwable throwable, Object[] args) {
		modify(snapshot -> {
			snapshot.setExpectThis(facade.serialize(self.getClass(), self));
			snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
			snapshot.setExpectException(facade.serialize(throwable.getClass(), throwable));
			snapshot.setExpectGlobals(globals.stream()
				.map(field -> facade.serialize(field, null))
				.toArray(SerializedField[]::new));
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
		}
	}

}
