package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;
import net.amygdalum.testrecorder.SnapshotInstrumentor;

public class InstrumentedClassLoader extends AbstractInstrumentedClassLoader {

	private SnapshotInstrumentor instrumentor;
	private String root;
	private Set<String> classes;

	public InstrumentedClassLoader(SnapshotInstrumentor instrumentor, Class<?> clazz, String... classes) {
		super(clazz.getClassLoader());
		this.instrumentor = createInstrumentor();
		this.root = clazz.getPackage().getName();
		this.classes = new HashSet<>(asList(classes));
	}
	
	private SnapshotInstrumentor createInstrumentor() {
		return new SnapshotInstrumentor(new DefaultTestRecorderAgentConfig());
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (!classes.contains(name)) {
			String enclosing = enclosingClassName(name);
			if (classes.contains(enclosing)) {
				Class<?> find  = findLoadedClass(name);
				if (find == null) {
					find = findClass(name);
				}
				return find;
			} else if (name.startsWith(root)) {
				return findClass(name);
			} else {
				return super.loadClass(name);
			}
		}
		
		try {
			byte[] bytes = instrumentor.instrument(name);
			return define(name, bytes);
		} catch (Throwable t) {
			throw new ClassNotFoundException(t.getMessage(), t);
		}

	}
	
}