package net.amygdalum.testrecorder;

public class ContextSnapshotFactory {
	
	public static final ContextSnapshotFactory NULL = new ContextSnapshotFactory("null", MethodSignature.NULL);
	
	private String key;
	private String className;
	private String methodName;
	private String methodDesc;

	private MethodSignature signature;


    private ContextSnapshotFactory(String key, MethodSignature signature) {
    	this.key = key;
		this.signature = signature;
	}

	public ContextSnapshotFactory(String key, String className, String methodName, String methodDesc) {
		this.key = key;
		this.className = className;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
    }

	public synchronized MethodSignature signature() {
		if (signature == null) {
			signature = MethodSignature.fromDescriptor(className, methodName, methodDesc);
		}
		return signature;
    }

	public ContextSnapshot createSnapshot() {
		return new ContextSnapshot(System.currentTimeMillis(), key, signature());
	}

}
