package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.Snapshot;

public class ObjectCycles {

	private ObjectCycles prev;
	private ObjectCycles next;
	
	public ObjectCycles() {
	}
	
	public void next(ObjectCycles next) {
		this.next = next;
		next.prev = this;
	}
	
	@Snapshot
	public ObjectCycles getNext() {
		return next;
	}
	
	@Snapshot
	public ObjectCycles getPrev() {
		return prev;
	}

}