package net.amygdalum.testrecorder.scenarios;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.amygdalum.testrecorder.profile.Recorded;

public class CollectionDataTypes {

	public CollectionDataTypes() {
	}

	@Recorded
	public List<Object> listsO(List<Object> ints, int i) {
		ints.add(i % 3);
		return ints;
	}

	@Recorded
	public List<Integer> listsI(List<Integer> ints, int i) {
		ints.add(i % 3);
		return ints;
	}

	@Recorded
	public Set<Object> setsO(Set<Object> ints, int i) {
		ints.add(i % 3);
		return ints;
	}

	@Recorded
	public Set<Integer> setsI(Set<Integer> ints, int i) {
		ints.add(i % 3);
		return ints;
	}

	@Recorded
	public Map<Object, Object> mapsO(Map<Object, Object> ints, int i) {
		ints.put(i, i % 3);
		return ints;
	}

	@Recorded
	public Map<Integer, Integer> mapsI(Map<Integer, Integer> ints, int i) {
		ints.put(i, i % 3);
		return ints;
	}

}