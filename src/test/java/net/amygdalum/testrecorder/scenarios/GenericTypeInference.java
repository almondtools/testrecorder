package net.amygdalum.testrecorder.scenarios;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.amygdalum.testrecorder.profile.Recorded;

public class GenericTypeInference {

	@Recorded
	public boolean containsValue(Map<String, Object> amap, BigDecimal avalue) {
		return amap.containsValue(avalue);
	}

	@Recorded
	public String getValue(Map<Object, String> amap, Object avalue) {
		return amap.get(avalue);
	}

	@Recorded
	public boolean addValue(Map<String, List<Object>> amap, String akey, String avalue) {
		return amap.computeIfAbsent(akey, key -> new ArrayList<>()).add(avalue);
	}

	@Recorded
	public boolean removeValue(Map<String, List<Object>> amap, String akey, List<String> list) {
		return amap.computeIfAbsent(akey, key -> new ArrayList<>()).removeAll(list);
	}

	@Recorded
	public boolean removeValue(String akey, List<String> list, Map<String, List<Object>> amap) {
		return amap.computeIfAbsent(akey, key -> new ArrayList<>()).removeAll(list);
	}

	@Recorded
	public boolean contains(List<Set<String>> sets, Object avalue) {
		boolean changed = false;
		for (Set<String> set : sets) {
			changed |= set.contains(avalue);
		}
		return changed;
	}

	@Recorded
	public boolean addValueToAll(List<List<Object>> lists, String avalue) {
		boolean changed = false;
		for (List<Object> list : lists) {
			changed |= list.add(avalue);
		}
		return changed;
	}

	@Recorded
	public boolean removeFromAll(List<List<Object>> lists, List<String> toremove) {
		boolean changed = false;
		for (List<Object> list : lists) {
			changed |= list.removeAll(toremove);
		}
		return changed;
	}

	@Recorded
	public boolean removeFromAllInverse(List<String> toremove, List<List<Object>> lists) {
		boolean changed = false;
		for (List<Object> list : lists) {
			changed |= list.removeAll(toremove);
		}
		return changed;
	}

}