package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class LeafType implements InnerType {

	private LeafMethods leafMethods;

	public LeafType(LeafMethods leafMethods) {
		this.leafMethods = leafMethods;
	}
	
	@Recorded
	public String quote(String string) {
		return "'" + string + "'";
	}

	@Override
	public String method() {
		return quote(leafMethods.toString());
	}

}
