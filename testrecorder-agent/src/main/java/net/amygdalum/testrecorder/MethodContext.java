package net.amygdalum.testrecorder;

import java.util.HashMap;
import java.util.Map;

import net.amygdalum.testrecorder.types.ContextSnapshot;
import net.amygdalum.testrecorder.types.VirtualMethodSignature;

public class MethodContext {

	private Map<String, ContextSnapshotFactory> factories;

	public MethodContext() {
		factories = new HashMap<>();
	}
	
	public void add(String signature, String className, String methodName, String methodDesc) {
		factories.put(signature, new ContextSnapshotFactory(signature, className, methodName, methodDesc));
	}

	public ContextSnapshot createSnapshot(String signature) {
		ContextSnapshotFactory factory = factories.getOrDefault(signature, ContextSnapshotFactory.NULL);
		return factory.createSnapshot();
	}

	public VirtualMethodSignature signature(String signature) {
		ContextSnapshotFactory factory = factories.getOrDefault(signature, ContextSnapshotFactory.NULL);
		return factory.signature();
	}

}
