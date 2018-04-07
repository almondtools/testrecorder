package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class SubBean extends SuperBean {

	private Object o;
	
	public SubBean() {
	}
	
	public void setO(Object o) {
		this.o = o;
	}
	
	@Recorded
	@Override
	public int hashCode() {
		int j = o == null ? 1 : o.hashCode();
		return getI() + 13 * j;
	}

}