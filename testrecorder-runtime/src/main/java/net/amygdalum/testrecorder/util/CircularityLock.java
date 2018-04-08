package net.amygdalum.testrecorder.util;

public class CircularityLock extends ThreadLocal<Boolean> {

    private static final Boolean NOT_ACQUIRED = null;

    public boolean acquire() {
        if (get() == NOT_ACQUIRED) {
            set(true);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean locked() {
    	return get() != NOT_ACQUIRED;
    }
    
    public void release() {
        set(NOT_ACQUIRED);
    }
}
