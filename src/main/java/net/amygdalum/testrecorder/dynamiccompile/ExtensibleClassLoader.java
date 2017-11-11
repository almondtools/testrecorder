package net.amygdalum.testrecorder.dynamiccompile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaFileObject.Kind;

import net.amygdalum.testrecorder.util.AbstractInstrumentedClassLoader;
import net.amygdalum.testrecorder.util.ClassInstrumenting;

public class ExtensibleClassLoader extends AbstractInstrumentedClassLoader {

	private Map<String, byte[]> defined;

	public ExtensibleClassLoader(ClassLoader loader) {
		super(loader);
		this.defined = new HashMap<>();
		adoptInstrumentations(loader);
		Thread.currentThread().setContextClassLoader(this);
	}

	@Override
	public Class<?> define(String name, byte[] bytes) {
		String resource = name.replace('.', '/') + Kind.CLASS.extension;
		defined.put(resource, bytes);
		return super.define(name, bytes);
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		byte[] bytes = defined.get(name);
		return bytes == null
			? super.getResourceAsStream(name)
			: new ByteArrayInputStream(bytes);
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