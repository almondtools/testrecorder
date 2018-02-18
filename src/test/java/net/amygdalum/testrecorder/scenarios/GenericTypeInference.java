package net.amygdalum.testrecorder.scenarios;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.amygdalum.testrecorder.Recorded;

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

}