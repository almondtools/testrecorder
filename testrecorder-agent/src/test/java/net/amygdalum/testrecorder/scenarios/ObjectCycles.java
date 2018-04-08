package net.amygdalum.testrecorder.scenarios;

import net.amygdalum.testrecorder.profile.Recorded;

public class ObjectCycles {

	private ObjectCycles prev;
	private ObjectCycles next;
	
	public ObjectCycles() {
	}
	
	public void next(ObjectCycles next) {
		this.next = next;
		next.prev = this;
	}
	
	@Recorded
	public ObjectCycles getNext() {
		return next;
	}
	
	@Recorded
	public ObjectCycles getPrev() {
		return prev;
	}

}