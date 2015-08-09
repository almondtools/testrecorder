package com.almondtools.iit.runtime;

import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class MapMatcher<K,V> extends TypeSafeMatcher<Map<K,V>>{

	public Matcher<?> entry(K key, V value) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void describeTo(Description description) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean matchesSafely(Map<K, V> item) {
		// TODO Auto-generated method stub
		return true;
	}

	public static <K,V> MapMatcher<K, V> noEntries() {
		return new MapMatcher<>();
	}

	public static <K,V> MapMatcher<K, V> containsEntries() {
		return new MapMatcher<>();
	}

}
