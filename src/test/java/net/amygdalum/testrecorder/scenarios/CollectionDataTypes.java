package net.amygdalum.testrecorder.scenarios;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.amygdalum.testrecorder.Snapshot;

public class CollectionDataTypes {

	public CollectionDataTypes() {
	}

	@Snapshot
	public List<Integer> lists(List<Integer> ints, int i) {
		ints.add(i % 3);
		return ints;
	}

	@Snapshot
	public Set<Integer> sets(Set<Integer> ints, int i) {
		ints.add(i % 3);
		return ints;
	}

	@Snapshot
	public Map<Integer, Integer> maps(Map<Integer, Integer> ints, int i) {
		ints.put(i, i % 3);
		return ints;
	}

}