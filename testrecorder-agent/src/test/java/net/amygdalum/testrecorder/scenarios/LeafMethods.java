package net.amygdalum.testrecorder.scenarios;

public class LeafMethods {

	private InnerType type;
	
	public LeafMethods() {
	}
	
	public void init(InnerType type) {
		this.type = type;
	}
	
	public String method() {
		return type.method();
	}
	
}
