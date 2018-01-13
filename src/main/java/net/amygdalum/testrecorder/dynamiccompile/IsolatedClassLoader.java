package net.amygdalum.testrecorder.dynamiccompile;

import static java.util.Arrays.asList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.tools.JavaFileObject.Kind;

import net.amygdalum.testrecorder.util.AbstractRedefiningClassLoader;

public class IsolatedClassLoader extends AbstractRedefiningClassLoader {

	private Map<String, byte[]> defined;
	private Set<String> names;

	public IsolatedClassLoader(ClassLoader loader, String... names) {
		super(loader);
		this.defined = new HashMap<>();
		this.names = new HashSet<>(asList(names));
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

	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (isRedefined(name)) {
			Class<?> find = findLoadedClass(name);
			if (find == null) {
				find = findClass(name);
			}
			return find;
		}
		String enclosing = enclosingClassName(name);
		if (isRedefined(enclosing)) {
			Class<?> find = findLoadedClass(name);
			if (find == null) {
				find = findClass(name);
			}
			return find;
		} else if (shouldBeRedefined(name)){
			try {
				byte[] bytes = getBytesForClass(name);
				return define(name, bytes);
			} catch (Throwable t) {
				throw new ClassNotFoundException(t.getMessage(), t);
			}
		} else {
			return super.loadClass(name);
		}
	}

	private boolean shouldBeRedefined(String name) {
		for (String namePart : names) {
			if (name.contains(namePart)) {
				return true;
			}
		}
		return false;
	}

	private byte[] getBytesForClass(String name) throws IOException {
		InputStream input = ClassLoader.getSystemResourceAsStream(name.replace('.', '/') + ".class");
		byte[] buffer = new byte[8192];
		int bytesRead;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
		return output.toByteArray();
	}

}