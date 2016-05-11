package net.amygdalum.testrecorder.dynamiccompile;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

import net.amygdalum.testrecorder.util.ClassInstrumenting;

public class JavaInMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {

	private List<JavaClassFileObject> files;
	private ClassLoader loader;

	public JavaInMemoryFileManager(ClassLoader loader, JavaFileManager fileManager) {
		super(fileManager);
		this.loader = new ExtensibleClassLoader(loader);
		this.files = new ArrayList<>();
	}

	public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling) throws IOException {
		if (kind == Kind.CLASS) {
			JavaClassFileObject file = new JavaClassFileObject(className);
			files.add(file);
			return file;
		} else {
			return super.getJavaFileForOutput(location, className, kind, sibling);
		}
	}

	@Override
	public ClassLoader getClassLoader(Location location) {
		if (loader instanceof ClassInstrumenting) {
			ClassInstrumenting instrumentedLoader = (ClassInstrumenting) loader;
			for (JavaClassFileObject file : files) {
				byte[] bytes = file.getBytes();
				if (bytes != null && bytes.length > 0) {
					String name = file.getClassName();
					if (!instrumentedLoader.isInstrumented(name)) {
						instrumentedLoader.define(name, bytes);
					}
				}
			}
		}
		files.clear();
		//the result loader it will be closed by the compiler, so do not return the loader to be reused
		return new URLClassLoader(new URL[0], loader);
	}

}