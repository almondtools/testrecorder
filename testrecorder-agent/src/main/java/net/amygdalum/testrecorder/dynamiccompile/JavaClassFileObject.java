package net.amygdalum.testrecorder.dynamiccompile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class JavaClassFileObject extends SimpleJavaFileObject {

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