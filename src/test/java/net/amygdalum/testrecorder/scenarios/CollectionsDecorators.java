package net.amygdalum.testrecorder.scenarios;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.amygdalum.testrecorder.Snapshot;

public class CollectionsDecorators {

	public CollectionsDecorators() {
	}

	@Snapshot
	public List<Object> unmodifiableList(List<Object> objects) {
		return Collections.unmodifiableList(objects);
	}

	@Snapshot
	public List<Object> synchronizedList(List<Object> objects) {
		return Collections.synchronizedList(objects);
	}

	@Snapshot
	public List<Object> checkedList(List<Object> objects) {
		return Collections.checkedList(objects, Object.class);
	}

	@Snapshot
	public List<Object> emptyList() {
		return Collections.emptyList();
	}

	@Snapshot
	public List<Object> singletonList(Object object) {
		return Collections.singletonList(object);
	}

	@Snapshot
	public Set<Object> unmodifiableSet(Set<Object> objects) {
		return Collections.unmodifiableSet(objects);
	}

	@Snapshot
	public Set<Object> synchronizedSet(Set<Object> objects) {
		return Collections.synchronizedSet(objects);
	}

	@Snapshot
	public Set<Object> checkedSet(Set<Object> objects) {
		return Collections.checkedSet(objects, Object.class);
	}

	@Snapshot
	public Set<Object> emptySet() {
		return Collections.emptySet();
	}

	@Snapshot
	public Set<Object> singletonSet(Object object) {
		return Collections.singleton(object);
	}

	@Snapshot
	public Map<Object,Object> unmodifiableMap(Map<Object,Object> objects) {
		return Collections.unmodifiableMap(objects);
	}

	@Snapshot
	public Map<Object,Object> synchronizedMap(Map<Object,Object> objects) {
		return Collections.synchronizedMap(objects);
	}

	@Snapshot
	public Map<Object,Object> checkedMap(Map<Object,Object> objects) {
		return Collections.checkedMap(objects, Object.class, Object.class);
	}

	@Snapshot
	public Map<Object,Object> emptyMap() {
		return Collections.emptyMap();
	}

	@Snapshot
	public Map<Object, Object> singletonMap(Object key, Object value) {
		return Collections.singletonMap(key, value);
	}

}