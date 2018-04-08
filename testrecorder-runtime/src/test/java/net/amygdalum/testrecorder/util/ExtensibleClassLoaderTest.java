package net.amygdalum.testrecorder.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.V1_8;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.bytebuddy.jar.asm.ClassWriter;

public class ExtensibleClassLoaderTest {

	private ExtensibleClassLoader classLoader;

	@BeforeEach
	public void before() throws Exception {
		classLoader = new ExtensibleClassLoader(ExtensibleClassLoaderTest.class.getClassLoader(), "test");
	}

	@Test
	public void testGetResourceAsStreamUncached() throws Exception {
		InputStream resourceAsStream = classLoader.getResourceAsStream("testresource.txt");
		
		assertThat(resourceAsStream).hasSameContentAs(new ByteArrayInputStream("testresource".getBytes()));
	}

	@Test
	public void testGetResourceAsStreamOnDefinedClass() throws Exception {
		ClassWriter classWriter = new ClassWriter(0);
		classWriter.visit(V1_8, ACC_PUBLIC, "MyClass", null, "java/lang/Object", null);
		byte[] bytes = classWriter.toByteArray();
		classLoader.define("MyClass", bytes);
		
		InputStream resourceAsStream = classLoader.getResourceAsStream("MyClass.class");

		assertThat(resourceAsStream).hasSameContentAs(new ByteArrayInputStream(bytes));
	}

	@Test
	public void testGetResourceAsStreamOnMockedResource() throws Exception {
		classLoader.defineResource("testresource.txt", "cachedtestresource".getBytes());
		
		InputStream resourceAsStream = classLoader.getResourceAsStream("testresource.txt");

		assertThat(resourceAsStream).hasSameContentAs(new ByteArrayInputStream("cachedtestresource".getBytes()));
	}

	@Test
	public void testLoadClassOnDefinedClass() throws Exception {
		ClassWriter myClass = new ClassWriter(0);
		myClass.visit(V1_8, ACC_PUBLIC, "MyClass", null, "java/lang/Object", null);
		classLoader.define("MyClass", myClass.toByteArray());
		
		Class<?> clazz = classLoader.loadClass("MyClass");

		assertThat(clazz.getName()).isEqualTo("MyClass");
	}

}
