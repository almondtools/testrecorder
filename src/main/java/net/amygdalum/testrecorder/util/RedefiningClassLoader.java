package net.amygdalum.testrecorder.util;

import java.util.Map;

public interface RedefiningClassLoader {

	Map<String, byte[]> getRedefinitions();

	Class<?> define(String name, byte[] bytes);

	boolean isRedefined(String name);

}
