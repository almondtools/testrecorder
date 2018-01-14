package net.amygdalum.testrecorder.util;

import net.amygdalum.testrecorder.Logger;

public final class Debug {

	private Debug() {
	}
	
	public static <T> T print(T object) {
		Logger.info(object);
		return object;
	}

}
