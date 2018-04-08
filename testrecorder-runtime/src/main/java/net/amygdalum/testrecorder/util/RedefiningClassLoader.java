package net.amygdalum.testrecorder.util;

public interface RedefiningClassLoader {

	Class<?> define(String name, byte[] bytes);

	boolean isRedefined(String name);

}
