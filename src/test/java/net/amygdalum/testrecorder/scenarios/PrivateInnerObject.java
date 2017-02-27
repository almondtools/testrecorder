package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Snapshot;

public class PrivateInnerObject {

	private InnerObject object;
	
	public PrivateInnerObject() {
		this.object = new InnerObject();
	}
	
	public String method(String data) {
		return object.method(data);
	}
	
	private class InnerObject {
		
		@Snapshot
		public String method(String data) {
			return data;
		}
		
	}

}