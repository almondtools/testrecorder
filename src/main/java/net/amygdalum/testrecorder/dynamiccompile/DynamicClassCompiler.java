package net.amygdalum.testrecorder.dynamiccompile;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

public class DynamicClassCompiler {

	private static final Pattern PACKAGE = Pattern.compile("package\\s+((\\w+\\s*\\.\\s*)*\\w+)\\s*;");
	private static final Pattern NAME = Pattern.compile("public\\s+class\\s+(\\w+)");

	private static ThreadLocal<Map<String, Class<?>>> compiled = ThreadLocal.withInitial(HashMap::new);

	private JavaCompiler compiler;

	public DynamicClassCompiler() {
		compiler = ToolProvider.getSystemJavaCompiler();
	}

	public Class<?> compile(String sourceCode) throws DynamicClassCompilerException {
		if (isCached(sourceCode)) {
			return fromCache(sourceCode);
		}

		String name = findName(sourceCode);
		String pkg = findPackage(sourceCode);
		String fullQualifiedName = pkg + '.' + name;

		JavaInMemoryFileManager fileManager = new JavaInMemoryFileManager(compiler.getStandardFileManager(null, null, null));
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, asList((JavaFileObject) new JavaSourceFileObject(name, sourceCode)));
		boolean success = task.call();
		if (!success) {
			throw new DynamicClassCompilerException("compile failed with messages", collectMessages(diagnostics.getDiagnostics()));
		}

		try {
			Class<?> clazz = fileManager.getClassLoader(null).loadClass(fullQualifiedName);
			cache(sourceCode, clazz);
			return clazz;
		} catch (ClassNotFoundException e) {
			throw new DynamicClassCompilerException("clazz " + fullQualifiedName + " cannot be loaded: " + e.getMessage());
		}
	}

	private List<String> collectMessages(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
		return diagnostics.stream()
			.map(diagnostic -> messageOf(diagnostic))
			.collect(toList());
	}

	private String messageOf(Diagnostic<? extends JavaFileObject> diagnostic) {
		return diagnostic.getLineNumber() + ":" + diagnostic.getColumnNumber()
			+ "\t" + diagnostic.getMessage(Locale.getDefault());
	}

	private boolean isCached(String sourceCode) {
		return compiled.get().containsKey(sourceCode);
	}

	private Class<?> fromCache(String sourceCode) {
		return compiled.get().get(sourceCode);
	}

	private void cache(String sourceCode, Class<?> clazz) {
		compiled.get().put(sourceCode, clazz);
	}

	private String findPackage(String item) throws DynamicClassCompilerException {
		Matcher packageMatcher = PACKAGE.matcher(item);
		boolean packageFound = packageMatcher.find();
		if (!packageFound) {
			throw new DynamicClassCompilerException("given code contains no package declaration");
		}
		return packageMatcher.group(1);
	}

	private String findName(String item) throws DynamicClassCompilerException {
		Matcher nameMatcher = NAME.matcher(item);
		boolean nameFound = nameMatcher.find();
		if (!nameFound) {
			throw new DynamicClassCompilerException("given code contains no public class");
		}
		return nameMatcher.group(1);
	}

	private static class JavaSourceFileObject extends SimpleJavaFileObject {

		private final String code;

		JavaSourceFileObject(String name, String code) {
			super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}
	}

	static class JavaClassFileObject extends SimpleJavaFileObject {

		private String name;
		private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

		public JavaClassFileObject(String name) {
			super(URI.create("string:///" + name.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
			this.name = name;
		}

		public byte[] getBytes() {
			return bos.toByteArray();
		}

		public String getClassName() {
			return name;
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
			return bos;
		}

	}

	private static class JavaInMemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {

		private List<JavaClassFileObject> files;

		public JavaInMemoryFileManager(JavaFileManager fileManager) {
			super(fileManager);
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
			return new DynamicClassLoader(files);
		}

	}
}
