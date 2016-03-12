package net.amygdalum.testrecorder.dynamiccompile;

import java.security.SecureClassLoader;
import java.util.List;

import net.amygdalum.testrecorder.dynamiccompile.DynamicClassCompiler.JavaClassFileObject;

public class DynamicClassLoader extends SecureClassLoader {

	private List<JavaClassFileObject> files;

	public DynamicClassLoader(List<JavaClassFileObject> files) {
		this.files = files;
	}

	public byte[] getBytes(String name) {
		for (JavaClassFileObject file : files) {
			if (file.getClassName().equals(name)) {
				return file.getBytes();
			}
		}
		return null;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] bytes = getBytes(name);
		if (bytes != null) {
			return super.defineClass(name, bytes, 0, bytes.length);
		} else {
			return super.findClass(name);
		}
	}

}