package net.amygdalum.testrecorder.util.testobjects;

public class Static {
    public static final String CONSTANT = "CONSTANT";
    public static String global;

	private Static() {
	}

	public static void setGlobal(String global) {
		Static.global = global;
	}
	
	public static String getGlobal() {
		return global;
	}
	
}