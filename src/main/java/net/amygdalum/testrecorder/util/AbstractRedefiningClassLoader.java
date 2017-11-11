package net.amygdalum.testrecorder.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;



public abstract class AbstractRedefiningClassLoader extends URLClassLoader implements RedefiningClassLoader {

	private Map<String, byte[]> redefinitions;

	public AbstractRedefiningClassLoader(ClassLoader loader) {
		super(extractUrls(loader), unwrap(loader));
		this.redefinitions = new LinkedHashMap<>();
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

	@Override
	public Map<String, byte[]> getRedefinitions() {
		return redefinitions;
	}

	@Override
	public boolean isRedefined(String name) {
		return redefinitions.containsKey(name);
	}

	@Override
	public Class<?> define(String name, byte[] bytes) {
		redefinitions.put(name, bytes);
		return defineClass(name, bytes, 0, bytes.length);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (redefinitions.containsKey(name) || redefinitions.containsKey(enclosingClassName(name))) {
			Class<?> find = findLoadedClass(name);
			if (find == null) {
				find = findClass(name);
			}
			return find;
		}
		return super.loadClass(name);
	}

	protected String enclosingClassName(String name) {
		int specialIndicator = name.indexOf('$');
		if (specialIndicator < 0) {
			return name;
		}
		return name.substring(0, specialIndicator);
	}

}
