package net.amygdalum.testrecorder.scenarios;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.amygdalum.testrecorder.profile.Recorded;

public class CollectionsDecorators {

	public CollectionsDecorators() {
	}

	@Recorded
	public void consume(Collection<?> list) {
	}

	@Recorded
	public void consume(Map<?,?> map) {
	}

	@Recorded
	public List<Object> unmodifiableList(List<Object> objects) {
		return Collections.unmodifiableList(objects);
	}

	@Recorded
	public List<Object> synchronizedList(List<Object> objects) {
		return Collections.synchronizedList(objects);
	}

	@Recorded
	public List<Object> checkedList(List<Object> objects) {
		return Collections.checkedList(objects, Object.class);
	}

	@Recorded
	public List<Object> emptyList() {
		return Collections.emptyList();
	}

	@Recorded
	public List<Object> singletonList(Object object) {
		return Collections.singletonList(object);
	}

	@Recorded
	public Set<Object> unmodifiableSet(Set<Object> objects) {
		return Collections.unmodifiableSet(objects);
	}

	@Recorded
	public Set<Object> synchronizedSet(Set<Object> objects) {
		return Collections.synchronizedSet(objects);
	}

	@Recorded
	public Set<Object> checkedSet(Set<Object> objects) {
		return Collections.checkedSet(objects, Object.class);
	}

	@Recorded
	public Set<Object> emptySet() {
		return Collections.emptySet();
	}

	@Recorded
	public Set<Object> singletonSet(Object object) {
		return Collections.singleton(object);
	}

	@Recorded
	public Map<Object,Object> unmodifiableMap(Map<Object,Object> objects) {
		return Collections.unmodifiableMap(objects);
	}

	@Recorded
	public Map<Object,Object> synchronizedMap(Map<Object,Object> objects) {
		return Collections.synchronizedMap(objects);
	}

	@Recorded
	public Map<Object,Object> checkedMap(Map<Object,Object> objects) {
		return Collections.checkedMap(objects, Object.class, Object.class);
	}

	@Recorded
	public Map<Object,Object> emptyMap() {
		return Collections.emptyMap();
	}

	@Recorded
	public Map<Object, Object> singletonMap(Object key, Object value) {
		return Collections.singletonMap(key, value);
	}

}