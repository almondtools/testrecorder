package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class AmbiguousConstructorBean {

	private int i;
	private int j;
	private Object o;
	
	public AmbiguousConstructorBean(int i, int j, Object o) {
		this.i = i;
		this.j = j;
		this.o = o;
	}
	
	public int getJ() {
		return j;
	}
	
	@Recorded
	@Override
	public int hashCode() {
		int j = o == null ? 1 : o.hashCode();
		return i + 13 * j;
	}

}