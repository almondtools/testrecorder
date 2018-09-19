package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.types.ContextSnapshot;
import net.amygdalum.testrecorder.types.VirtualMethodSignature;

public class ContextSnapshotFactory {
	
	public static final ContextSnapshotFactory NULL = new ContextSnapshotFactory("null", VirtualMethodSignature.NULL);
	
	private String key;
	private String className;
	private String methodName;
	private String methodDesc;

	private VirtualMethodSignature signature;


    private ContextSnapshotFactory(String key, VirtualMethodSignature signature) {
    	this.key = key;
		this.signature = signature;
	}

	public ContextSnapshotFactory(String key, String className, String methodName, String methodDesc) {
		this.key = key;
		this.className = className;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
    }

	public synchronized VirtualMethodSignature signature() {
		if (signature == null) {
			signature = VirtualMethodSignature.fromDescriptor(className, methodName, methodDesc);
		}
		return signature;
    }

	public ContextSnapshot createSnapshot() {
		return new ContextSnapshot(System.currentTimeMillis(), key, signature());
	}

}
