package com.almondtools.testrecorder;

import static com.almondtools.testrecorder.ConfigRegistry.loadConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class SnapshotGenerator {

	private static ThreadFactory THREADS = new ThreadFactory() {
		
		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = Executors.defaultThreadFactory().newThread(runnable);
			thread.setDaemon(true);
			return thread;
		}
	
	};
	
	private Object self;

	private ExecutorService executor;
	private Map<String, MethodSnapshotFactory> methodSnapshots;
	private Map<String, ValueSnapshotFactory> valueSnapshots;
	private ThreadLocal<MethodSnapshot> current = new ThreadLocal<>();
	private ThreadLocal<SerializerFacade> currentFacade = new ThreadLocal<>();

	private MethodSnapshotConsumer methodConsumer;
	private ValueSnapshotConsumer valueConsumer;
	private long timeoutInMillis;
	

	public SnapshotGenerator(Object self, Class<? extends SnapshotConfig> config) {
		this.methodConsumer = loadConfig(config).getMethodConsumer();
		this.valueConsumer = loadConfig(config).getValueConsumer();
		this.timeoutInMillis = loadConfig(config).getTimeoutInMillis();
		
		this.self = self;

		this.executor = Executors.newSingleThreadExecutor(THREADS);
		this.methodSnapshots = new HashMap<>();
		this.valueSnapshots = new HashMap<>();
	}
	
	public MethodSnapshotConsumer getMethodConsumer() {
		return methodConsumer;
	}

	public ValueSnapshotConsumer getValueConsumer() {
		return valueConsumer;
	}

	public void register(String signature, Method method) {
		MethodSnapshotFactory factory = new MethodSnapshotFactory(method.getDeclaringClass(), method.getAnnotation(Snapshot.class), method.getGenericReturnType(), method.getName(), method.getGenericParameterTypes());
		methodSnapshots.put(signature, factory);
	}

	public void register(String signature, Field field) {
		ValueSnapshotFactory factory = new ValueSnapshotFactory(field.getDeclaringClass(), field.getAnnotation(Snapshot.class), field.getGenericType(), field.getName());
		valueSnapshots.put(signature, factory);
	}

	public ValueSnapshot valueSnapshot(String signature) {
		return valueSnapshots.get(signature).createSnapshot();
	}

	public MethodSnapshot newSnapshot(String signature) {
		MethodSnapshot snapshot = methodSnapshots.get(signature).createSnapshot();
		current.set(snapshot);
		return snapshot;
	}

	public MethodSnapshot fetchSnapshot() {
		return current.get();
	}

	public SerializerFacade facade(String signature) {
		ConfigurableSerializerFacade facade = new ConfigurableSerializerFacade(methodSnapshots.get(signature).profile());
		currentFacade.set(facade);
		return facade;
	}

	public SerializerFacade facade() {
		SerializerFacade serializerFacade = currentFacade.get();
		serializerFacade.reset();
		return serializerFacade;
	}

	public SerializerFacade valueFacade(String signature) {
		ConfigurableSerializerFacade facade = new ConfigurableSerializerFacade(valueSnapshots.get(signature).profile());
		return facade;
	}

	public void storeValue(String signature, Object value) {
		SerializerFacade facade = valueFacade(signature);
		ValueSnapshot valueSnapshot = valueSnapshot(signature);
		modify(valueSnapshot, snapshot -> {
			snapshot.setValue(facade.serialize(valueSnapshot.getType(), value));
		});
		consume(valueSnapshot);
	}
	
	public void setupVariables(String signature, Object... args) {
		SerializerFacade facade = facade(signature);
		modify(newSnapshot(signature), snapshot -> {
			snapshot.setSetupThis(facade.serialize(self.getClass(), self));
			snapshot.setSetupArgs(facade.serialize(snapshot.getArgumentTypes(), args));
		});
	}

	public void expectVariables(Object result, Object... args) {
		SerializerFacade facade = facade();
		MethodSnapshot currentSnapshot = fetchSnapshot();
		modify(currentSnapshot, snapshot -> {
			snapshot.setExpectThis(facade.serialize(self.getClass(), self));
			snapshot.setExpectResult(facade.serialize(snapshot.getResultType(), result));
			snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
		});
		consume(currentSnapshot);
	}

	public void expectVariables(Object... args) {
		SerializerFacade facade = facade();
		MethodSnapshot currentSnapshot = fetchSnapshot();
		modify(currentSnapshot, snapshot -> {
			snapshot.setExpectThis(facade.serialize(self.getClass(), self));
			snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
		});
		consume(currentSnapshot);
	}

	public void throwVariables(Throwable throwable, Object... args) {
		SerializerFacade facade = facade();
		MethodSnapshot currentSnapshot = fetchSnapshot();
		modify(currentSnapshot, snapshot -> {
			snapshot.setExpectThis(facade.serialize(self.getClass(), self));
			snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
			snapshot.setExpectException(facade.serialize(throwable.getClass(), throwable));
		});
		consume(currentSnapshot);
	}

	private void consume(MethodSnapshot snapshot) {
		if (snapshot.isValid()) {
			if (methodConsumer != null) {
				methodConsumer.accept(snapshot);
			}
		}
	}

	private void consume(ValueSnapshot snapshot) {
		if (snapshot.isValid()) {
			if (valueConsumer != null) {
				valueConsumer.accept(snapshot);
			}
		}
	}

	private void modify(MethodSnapshot snapshot, Consumer<MethodSnapshot> task) {
		try {
			Future<?> future = executor.submit(() -> {
				task.accept(snapshot);
			});
			future.get(timeoutInMillis, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException | CancellationException e) {
			snapshot.invalidate();
		}
	}

	private void modify(ValueSnapshot snapshot, Consumer<ValueSnapshot> task) {
		try {
			Future<?> future = executor.submit(() -> {
				task.accept(snapshot);
			});
			future.get(timeoutInMillis, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException | CancellationException e) {
			snapshot.invalidate();
		}
	}

}
