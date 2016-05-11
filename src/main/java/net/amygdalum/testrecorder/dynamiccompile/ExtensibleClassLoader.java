package net.amygdalum.testrecorder.dynamiccompile;

import java.util.Map;

import net.amygdalum.testrecorder.util.AbstractInstrumentedClassLoader;
import net.amygdalum.testrecorder.util.ClassInstrumenting;

public class ExtensibleClassLoader extends AbstractInstrumentedClassLoader {

	public ExtensibleClassLoader(ClassLoader loader) {
		super(loader);
		adoptInstrumentations(loader);
		Thread.currentThread().setContextClassLoader(this);
	}

	private final void adoptInstrumentations(ClassLoader loader) {
		if (loader instanceof ClassInstrumenting) {
			Map<String, byte[]> instrumentations = ((ClassInstrumenting) loader).getInstrumentations();
			for (Map.Entry<String, byte[]> instrumentation : instrumentations.entrySet()) {
				String name = instrumentation.getKey();
				byte[] bytes = instrumentation.getValue();
				if (findLoadedClass(name) == null) {
					define(name, bytes);
				}
			}
		}
	}

}