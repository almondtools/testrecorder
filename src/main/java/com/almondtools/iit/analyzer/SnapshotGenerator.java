package com.almondtools.iit.analyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.almondtools.iit.GeneratedSnapshot;

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
		GeneratedSnapshot snapshot = newSnapshot(signature);
		snapshot.setupThis(self);
		snapshot.setupArgs(args);
	}

	public void expectVariables(Object result, Object... args) {
		GeneratedSnapshot snapshot = fetchSnapshot();
		snapshot.expectThis(self);
		snapshot.expectResult(result);
		snapshot.expectArgs(args);
		consumer.accept(snapshot);
	}
	
	public void expectVariables(Object... args) {
		GeneratedSnapshot snapshot = fetchSnapshot();
		snapshot.expectThis(self);
		snapshot.expectArgs(args);
		consumer.accept(snapshot);
	}
	
	public void throwVariables(Throwable throwable, Object... args) {
		GeneratedSnapshot snapshot = fetchSnapshot();
		snapshot.expectThis(self);
		snapshot.expectException(throwable);
		snapshot.expectArgs(args);
		consumer.accept(snapshot);
	}
	
}
