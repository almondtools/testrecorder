package com.almondtools.testrecorder;

import java.util.LinkedHashMap;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class MapMatcher<K,V> extends TypeSafeMatcher<Map<K,V>>{
	
	private Map<K,V> entries;
	
	public MapMatcher() {
		entries = new LinkedHashMap<>();
	}

	public MapMatcher<K,V> entry(K key, V value) {
		entries.put(key, value);
		return this;
	}

	@Override
	public void describeTo(Description description) {
		description.appendValue(entries);
	}

	@Override
	protected boolean matchesSafely(Map<K, V> item) {
		if (item.size() != entries.size()) {
			return false;
		}
		for (Map.Entry<K, V> entry : item.entrySet()) {
			K key = entry.getKey();
			V value = entry.getValue();
			
			V expectedValue = entries.get(key);
			if (expectedValue == null && value == null) {
				continue;
			} else if (expectedValue == null && value != null) {
				return false;
			} else if (!expectedValue.equals(value)) {
				return false;
			}
		}
		
		return true;
	}

	public static <K,V> MapMatcher<K, V> noEntries(Class<K> key, Class<V> value) {
		return new MapMatcher<>();
	}

	public static <K,V> MapMatcher<K, V> containsEntries(Class<K> key, Class<V> value) {
		return new MapMatcher<>();
	}

}
