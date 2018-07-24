package net.amygdalum.testrecorder.util.testobjects;

public class ConstructorCycle {

    public ConstructorCycle next;

    public ConstructorCycle(ConstructorCycle next, boolean allownull) {
    	if (!allownull && next == null) {
    		throw new IllegalArgumentException();
    	}
        this.next = next;
    }

    public ConstructorCycle getNext() {
		return next;
	}

}