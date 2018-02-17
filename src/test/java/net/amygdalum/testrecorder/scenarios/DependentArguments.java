package net.amygdalum.testrecorder.scenarios;

import java.math.BigDecimal;
import java.util.Map;

import net.amygdalum.testrecorder.Recorded;

public class DependentArguments {

	@Recorded
	public boolean containsValue(Map<String, Object> amap, BigDecimal avalue) {
		return amap.containsValue(avalue);
	}

	@Recorded
	public String getValue(Map<Object, String> amap, Object avalue) {
		return amap.get(avalue);
	}

}