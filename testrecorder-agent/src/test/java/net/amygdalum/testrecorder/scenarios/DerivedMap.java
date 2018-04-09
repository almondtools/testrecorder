package net.amygdalum.testrecorder.scenarios;

import java.util.LinkedHashMap;

import net.amygdalum.testrecorder.profile.Recorded;

public class DerivedMap<K, V> extends LinkedHashMap<K, V> {

	@Recorded
	@Override
	public V put(K key, V value) {
		return super.put(key, value);
	}
}
