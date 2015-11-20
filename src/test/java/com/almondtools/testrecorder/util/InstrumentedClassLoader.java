package com.almondtools.testrecorder.util;

import static java.util.Arrays.asList;

import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import com.almondtools.testrecorder.DefaultConfig;
import com.almondtools.testrecorder.SnapshotInstrumentor;

public class InstrumentedClassLoader extends URLClassLoader {

	private SnapshotInstrumentor instrumentor;
	private String root;
	private Set<String> classes;

	public InstrumentedClassLoader(SnapshotInstrumentor instrumentor, String root, String... classes) {
		super(((URLClassLoader) getSystemClassLoader()).getURLs());
		this.instrumentor = createInstrumentor();
		this.root = root;
		this.classes = new HashSet<>(asList(classes));
	}

	private SnapshotInstrumentor createInstrumentor() {
		SnapshotInstrumentor snapshotInstrumentor = new SnapshotInstrumentor(new DefaultConfig());
		return snapshotInstrumentor;
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (name.equals(root)) {
			return findClass(name);
		}
		if (!classes.contains(name)) {
			return super.loadClass(name);
		}

		try {
			byte[] bytes = instrumentor.instrument(name);

			return defineClass(name, bytes, 0, bytes.length);
		} catch (Throwable t) {
			throw new ClassNotFoundException(t.getMessage(), t);
		}

	}
}