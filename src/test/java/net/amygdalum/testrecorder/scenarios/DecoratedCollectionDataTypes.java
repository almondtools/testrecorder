package net.amygdalum.testrecorder.scenarios;

import java.util.Collections;
import java.util.List;

import net.amygdalum.testrecorder.Snapshot;

public class DecoratedCollectionDataTypes {

	public DecoratedCollectionDataTypes() {
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

}