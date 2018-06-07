package net.amygdalum.testrecorder.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.V1_8;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.tools.JavaFileObject.Kind;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.bytebuddy.jar.asm.ClassWriter;

public class ExtensibleClassLoaderTest {

	private ExtensibleClassLoader classLoader;

	@BeforeEach
	void before() throws Exception {
		classLoader = new ExtensibleClassLoader(ExtensibleClassLoaderTest.class.getClassLoader(), "test");
	}

	@Test
	void testGetResourceAsStreamUncached() throws Exception {
		InputStream resourceAsStream = classLoader.getResourceAsStream("testresource.txt");

		assertThat(resourceAsStream).hasSameContentAs(new ByteArrayInputStream("testresource".getBytes()));
	}

	@Test
	void testGetResourceAsStreamOnDefinedClass() throws Exception {
		ClassWriter classWriter = new ClassWriter(0);
		classWriter.visit(V1_8, ACC_PUBLIC, "MyClass", null, "java/lang/Object", null);
		byte[] bytes = classWriter.toByteArray();
		classLoader.define("MyClass", bytes);

		InputStream resourceAsStream = classLoader.getResourceAsStream("MyClass.class");

		assertThat(resourceAsStream).hasSameContentAs(new ByteArrayInputStream(bytes));
	}

	@Test
	void testGetResourceAsStreamOnMockedResource() throws Exception {
		classLoader.defineResource("testresource.txt", "cachedtestresource".getBytes());

		InputStream resourceAsStream = classLoader.getResourceAsStream("testresource.txt");

		assertThat(resourceAsStream).hasSameContentAs(new ByteArrayInputStream("cachedtestresource".getBytes()));
	}

	@Test
	void testLoadClassOnDefinedClass() throws Exception {
		ClassWriter myClass = new ClassWriter(0);
		myClass.visit(V1_8, ACC_PUBLIC, "MyClass", null, "java/lang/Object", null);
		classLoader.define("MyClass", myClass.toByteArray());

		Class<?> clazz = classLoader.loadClass("MyClass");

		assertThat(clazz.getName()).isEqualTo("MyClass");
	}

	@Test
	void testShouldBeRedefined() throws Exception {
		assertThat(classLoader.shouldBeRedefined("externalpackage")).isFalse();
	}

	@Test
	void testShouldBeRedefinedInternalPackage() throws Exception {
		classLoader.addPackage("internalpackage");

		assertThat(classLoader.shouldBeRedefined("internalpackage")).isTrue();
	}

	@Test
	public void testUnwrap() throws Exception {
		assertThat(classLoader.getParent()).isSameAs(ExtensibleClassLoader.class.getClassLoader());
	}

	@SuppressWarnings("resource")
	@Test
	public void testUnwrapOnRedefiningClassLoader() throws Exception {
		ExtensibleClassLoader childclassloader = new ExtensibleClassLoader(classLoader);

		assertThat(childclassloader.getParent()).isSameAs(classLoader.getParent());
	}

	@Test
	public void testIsRedefined() throws Exception {
		assertThat(classLoader.isRedefined(Simple.class.getName())).isFalse();
	}

	@Test
	public void testIsRedefinedOnRedefined() throws Exception {
		classLoader.redefineClass(Simple.class.getName());
		
		assertThat(classLoader.isRedefined(Simple.class.getName())).isTrue();
	}

	@Test
	public void testGetResources() throws Exception {
		String resource = Simple.class.getName().replace('.', '/') + Kind.CLASS.extension;
		
		assertThat(classLoader.getResources(resource).nextElement().toString()).endsWith("net/amygdalum/testrecorder/util/testobjects/Simple.class");
	}
	
	@Test
	public void testGetResourcesRedefined() throws Exception {
		String resource = Simple.class.getName().replace('.', '/') + Kind.CLASS.extension;
		classLoader.redefineClass(Simple.class.getName());
		
		assertThat(classLoader.getResources(resource).nextElement().toString()).doesNotEndWith("net/amygdalum/testrecorder/util/testobjects/Simple.class");
	}
	
}
