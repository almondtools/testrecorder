package net.amygdalum.testrecorder.util;

public interface OutputListener {

	void notifyOutput(Class<?> clazz, String method, Object... args);

}