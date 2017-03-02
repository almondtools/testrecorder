package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Snapshot;

public class RejectedCases {

	public RejectedCases() {
	}
	
	@Snapshot
	private String privateMethod(String data) {
		return data.toUpperCase();
	}
	
	@Snapshot
	protected String protectedMethod(String data) {
		return data.toUpperCase();
	}
	
	@Snapshot
	protected String packagePrivateMethod(String data) {
		return data.toUpperCase();
	}
	
	public void rejected() {
		privateMethod("data");
		new PrivateObject().method("data");
	}

	public void recorded() {
		packagePrivateMethod("data");
		protectedMethod("data");
		new PackagePrivateObject().method("data");
		new ProtectedObject().method("data");
	}

	private static class PrivateObject {
		
		@Snapshot
		public String method(String data) {
			return data.toUpperCase();
		}
			
	}

	static class PackagePrivateObject {
		
		@Snapshot
		public String method(String data) {
			return data.toUpperCase();
		}
				
	}

	protected static class ProtectedObject {
		
		@Snapshot
		public String method(String data) {
			return data.toUpperCase();
		}
				
	}

}