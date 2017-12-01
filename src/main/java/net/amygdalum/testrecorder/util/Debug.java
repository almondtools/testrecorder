package net.amygdalum.testrecorder.util;

public final class Debug {

	private Debug() {
		
	}
	
	public static <T> T print(T object) {
		System.out.println(object);
		return object;
	}

}
