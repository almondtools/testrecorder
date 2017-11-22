package net.amygdalum.testrecorder;

public class ContextSnapshotFactory {

	private SerializationProfile profile;
	
	private String className;
	private String methodName;
	private String methodDesc;

	private MethodSignature signature;

	public ContextSnapshotFactory(SerializationProfile profile, String className, String methodName, String methodDesc) {
		this.profile = profile;
		this.className = className;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
    }

    public SerializationProfile profile() {
		return profile;
	}

	public ContextSnapshot createSnapshot() {
		if (signature == null) {
			try {
				signature = MethodSignature.fromDescriptor(className, methodName, methodDesc);
			} catch (ReflectiveOperationException e) {
				throw new SerializationException(e);
			}
		}
		return new ContextSnapshot(System.currentTimeMillis(), signature);
	}

}
