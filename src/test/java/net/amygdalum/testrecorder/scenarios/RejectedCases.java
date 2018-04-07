package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class RejectedCases {

	public RejectedCases() {
	}
	
	@Recorded
	private String privateMethod(String data) {
		return data.toUpperCase();
	}
	
	@Recorded
	protected String protectedMethod(String data) {
		return data.toUpperCase();
	}
	
	@Recorded
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
		
		@Recorded
		public String method(String data) {
			return data.toUpperCase();
		}
			
	}

	static class PackagePrivateObject {
		
		@Recorded
		public String method(String data) {
			return data.toUpperCase();
		}
				
	}

	protected static class ProtectedObject {
		
		@Recorded
		public String method(String data) {
			return data.toUpperCase();
		}
				
	}

}