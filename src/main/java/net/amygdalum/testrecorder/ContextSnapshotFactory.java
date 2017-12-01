package net.amygdalum.testrecorder;

import net.amygdalum.testrecorder.profile.SerializationProfile;

public class ContextSnapshotFactory {

	private SerializationProfile profile;
	
	private String key;
	private String className;
	private String methodName;
	private String methodDesc;

	private MethodSignature signature;


	public ContextSnapshotFactory(SerializationProfile profile, String key, String className, String methodName, String methodDesc) {
		this.profile = profile;
		this.key = key;
		this.className = className;
		this.methodName = methodName;
		this.methodDesc = methodDesc;
    }

    public SerializationProfile profile() {
		return profile;
	}

	public ContextSnapshot createSnapshot() {
		if (signature == null) {
			signature = MethodSignature.fromDescriptor(className, methodName, methodDesc);
		}
		return new ContextSnapshot(System.currentTimeMillis(), key, signature);
	}

}
