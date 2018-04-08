package net.amygdalum.testrecorder.dynamiccompile;

public class CompilationUnit {

	private ClassLoader loader;
	private String name;
	private String pkg;

	public CompilationUnit(ClassLoader loader, String pkg, String name) {
		this.loader = loader;
		this.name = name;
		this.pkg = pkg;
	}
	
	public ClassLoader getLoader() {
		return loader;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFullQualifiedName() {
		return pkg + '.' + name;
	}

}
