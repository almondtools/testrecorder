package net.amygdalum.testrecorder.util;

import java.util.Map;

public interface ClassInstrumenting {

	Map<String, byte[]> getInstrumentations();

	Class<?> define(String name, byte[] bytes);

	boolean isInstrumented(String name);

}
