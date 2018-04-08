package net.amygdalum.testrecorder.scenarios;

import java.util.Set;

import net.amygdalum.testrecorder.profile.Recorded;

public class GenericFields {

	private Set<String> set;
	
	public GenericFields() {
	}

	public void setSet(Set<String> set) {
		this.set = set;
	}
	
	@Recorded
	@Override
	public int hashCode() {
		return set == null ? 1 : set.hashCode();
	}

}