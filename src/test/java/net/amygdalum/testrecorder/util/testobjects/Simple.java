package net.amygdalum.testrecorder.util.testobjects;

public class Simple {
	private String str;

	public Simple() {
	}

	public Simple(String str) {
		this.str = str;
	}

	public String getStr() {
		return str;
	}

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}