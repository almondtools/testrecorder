package com.almondtools.invivoderived.analyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.almondtools.invivoderived.GeneratedSnapshot;
import com.almondtools.invivoderived.GeneratedSnapshotFactory;
import com.almondtools.invivoderived.SerializerFacade;

public class SnapshotGenerator {

	private static Consumer<GeneratedSnapshot> consumer = snapshot -> {};

	private Object self;
	
	private Map<String, GeneratedSnapshotFactory> snapshotFactories;
	private ThreadLocal<GeneratedSnapshot> current = new ThreadLocal<>();

	public SnapshotGenerator(Object self) {
		this.self = self;
		this.snapshotFactories = new HashMap<>();
	}
	
	public static void setSnapshotConsumer(Consumer<GeneratedSnapshot> consumer) {
		SnapshotGenerator.consumer = consumer;
	}
	
	public void register(String signature,  Class<?> resultType, String methodName, Class<?>... argumentTypes) {
		snapshotFactories.put(signature, new GeneratedSnapshotFactory(resultType, methodName, argumentTypes));
	}

	public GeneratedSnapshot newSnapshot(String signature) {
		GeneratedSnapshot snapshot = snapshotFactories.get(signature).create();
		current.set(snapshot);
		return snapshot;
	}
	
	public GeneratedSnapshot fetchSnapshot() {
		return current.get();
	}
	
	public void setupVariables(String signature, Object... args) {
		SerializerFacade facade = new SerializerFacade();
		GeneratedSnapshot snapshot = newSnapshot(signature);
		snapshot.setSetupThis(facade.serialize(self.getClass(), self));
		snapshot.setSetupArgs(facade.serialize(snapshot.getArgumentTypes(), args));
	}

	public void expectVariables(Object result, Object... args) {
		SerializerFacade facade = new SerializerFacade();
		GeneratedSnapshot snapshot = fetchSnapshot();
		snapshot.setExpectThis(facade.serialize(self.getClass(), self));
		snapshot.setExpectResult(facade.serialize(snapshot.getResultType(), result));
		snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
		consumer.accept(snapshot);
	}
	
	public void expectVariables(Object... args) {
		SerializerFacade facade = new SerializerFacade();
		GeneratedSnapshot snapshot = fetchSnapshot();
		snapshot.setExpectThis(facade.serialize(self.getClass(), self));
		snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
		consumer.accept(snapshot);
	}
	
	public void throwVariables(Throwable throwable, Object... args) {
		SerializerFacade facade = new SerializerFacade();
		GeneratedSnapshot snapshot = fetchSnapshot();
		snapshot.setExpectThis(facade.serialize(self.getClass(), self));
		snapshot.setExpectArgs(facade.serialize(snapshot.getArgumentTypes(), args));
		snapshot.setExpectException(facade.serialize(throwable.getClass(), throwable));
		consumer.accept(snapshot);
	}

}
