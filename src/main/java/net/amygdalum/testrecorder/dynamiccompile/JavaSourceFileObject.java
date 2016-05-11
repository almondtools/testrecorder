package net.amygdalum.testrecorder.dynamiccompile;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class JavaSourceFileObject extends SimpleJavaFileObject {

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