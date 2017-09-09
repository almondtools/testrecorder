package net.amygdalum.testrecorder.util.testobjects;

public class SimpleMisleadingFieldName {

	private int str;

	public SimpleMisleadingFieldName(int str) {
		this.str = str;
	}

	public int getStr() {
		return str;
	}

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}