package net.amygdalum.testrecorder.util;

import static java.util.Arrays.asList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.tools.JavaFileObject.Kind;



public class ExtensibleClassLoader extends URLClassLoader implements RedefiningClassLoader {

	protected Set<String> packages;
	private Map<String, byte[]> resources;

	public ExtensibleClassLoader(ClassLoader loader, String... packages) {
		super(extractUrls(loader), unwrap(loader));
		this.packages = new HashSet<>(asList(packages));
		this.resources = new LinkedHashMap<>();
	}

	private static URL[] extractUrls(ClassLoader loader) {
		Set<URL> urls = new LinkedHashSet<>();
		while (loader != null) {
			if (loader instanceof URLClassLoader) {
				for (URL url : ((URLClassLoader) loader).getURLs()) {
					urls.add(url);
				}
			}
			loader = loader.getParent();
		}
		return urls.toArray(new URL[0]);
	}

	private static ClassLoader unwrap(ClassLoader loader) {
		if (loader instanceof RedefiningClassLoader) {
			return loader.getParent();
		} else {
			return loader;
		}
	}

	public void addPackage(String pkg) {
		packages.add(pkg);
	}

	protected boolean shouldBeRedefined(String name) {
		for (String pkg : packages) {
			if (name.startsWith(pkg)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isRedefined(String name) {
		String resource = classToResource(name);
		return resources.containsKey(resource);
	}

	@Override
	public Class<?> define(String name, byte[] bytes) {
		String resource = classToResource(name);
		resources.put(resource, bytes);
		return defineClass(name, bytes, 0, bytes.length);
	}

	public void defineResource(String resource, byte[] content) {
		resources.put(resource, content);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		String resource = classToResource(name);
		if (resources.containsKey(resource) || resources.containsKey(enclosingClassName(resource))) {
			return findRedefinedClass(name);
		} else if (shouldBeRedefined(name)) {
			return redefineClass(name);
		}
		return super.loadClass(name);
	}

	private String enclosingClassName(String name) {
		int specialIndicator = name.indexOf('$');
		if (specialIndicator < 0) {
			return name;
		}
		return name.substring(0, specialIndicator);
	}

	public Class<?> findRedefinedClass(String name) throws ClassNotFoundException {
		Class<?> find = findLoadedClass(name);
		if (find != null) {
			return find;
		}
		return findClass(name);
	}

	public Class<?> redefineClass(String name) throws ClassNotFoundException {
		try {
			byte[] bytes = getBytesForClass(name);
			return define(name, bytes);
		} catch (Throwable t) {
			throw new ClassNotFoundException(t.getMessage(), t);
		}
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

	@Override
	public InputStream getResourceAsStream(String name) {
		byte[] bytes = resources.get(name);
		if (bytes != null) {
			return new ByteArrayInputStream(bytes);
		}
		return super.getResourceAsStream(name);
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		if (resources.containsKey(name)) {
			int lastDot = name.lastIndexOf('.');
			String fileName = name.substring(lastDot + 1);
			Path file = Files.createTempFile(fileName, "");
			Files.write(file, resources.get(name));
			URL url = file.toUri().toURL();
			return Collections.enumeration(asList(url));
		}
		return super.getResources(name);
	}

	private String classToResource(String name) {
		return name.replace('.', '/') + Kind.CLASS.extension;
	}

}
