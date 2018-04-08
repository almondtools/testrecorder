package net.amygdalum.testrecorder.util;

import java.util.function.Function;

public final class Debug {

	private Debug() {
	}
	
	public static <T> T print(T object) {
		Logger.info(object);
		return object;
	}

	public static <T,S> T print(T object, Function<T,S> mapping) {
		S part = mapping.apply(object);
		Logger.info(part);
		return object;
	}

}
