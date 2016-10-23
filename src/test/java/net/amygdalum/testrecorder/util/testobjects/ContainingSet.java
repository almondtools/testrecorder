package net.amygdalum.testrecorder.util.testobjects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ContainingSet {

	private Set<String> set;

	public ContainingSet() {
	}

	public ContainingSet(Collection<String> set) {
		this.set = new HashSet<>(set);
	}

	public Set<String> getSet() {
		return set;
	}
}