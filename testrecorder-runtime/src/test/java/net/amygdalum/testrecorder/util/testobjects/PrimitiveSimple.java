package net.amygdalum.testrecorder.util.testobjects;

public class PrimitiveSimple {
	private int i;

	public PrimitiveSimple() {
	}

	public int getI() {
		return i;
	}
	
	public void setI(int i) {
		this.i = i;
	}

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}