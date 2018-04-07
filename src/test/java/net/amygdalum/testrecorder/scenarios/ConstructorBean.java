package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class ConstructorBean {

	private int i;
	private Object o;
	
	public ConstructorBean(int i, Object o) {
		this.i = i;
		this.o = o;
	}
	
	@Recorded
	@Override
	public int hashCode() {
		int j = o == null ? 1 : o.hashCode();
		return i + 13 * j;
	}

}