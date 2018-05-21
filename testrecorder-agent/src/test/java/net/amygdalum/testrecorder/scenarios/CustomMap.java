package net.amygdalum.testrecorder.scenarios;

import java.util.HashMap;

import net.amygdalum.testrecorder.profile.Recorded;

public class CustomMap<K, V> extends HashMap<K, V> {

	@Recorded
	@Override
	public V put(K k, V v) {
		return super.put(k, v);
	}

}
