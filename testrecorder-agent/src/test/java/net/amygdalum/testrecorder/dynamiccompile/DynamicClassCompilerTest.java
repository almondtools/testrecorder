package net.amygdalum.testrecorder.dynamiccompile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DynamicClassCompilerTest {

	private DynamicClassCompiler dynamicClassCompiler;

	@BeforeEach
	public void before() throws Exception {
		dynamicClassCompiler = new DynamicClassCompiler();
	}

	@Test
	void testCompile() throws Exception {
		Class<?> clazz = dynamicClassCompiler.compile(""
			+ "package net.amygdalum.testrecorder.nopackage;"
			+ "public class MyClass {}", DynamicClassCompilerTest.class.getClassLoader());
		
		assertThat(clazz.getName()).isEqualTo("net.amygdalum.testrecorder.nopackage.MyClass");
	}

	@Test
	void testCompileNoPackage() throws Exception {
		assertThatThrownBy(() -> dynamicClassCompiler.compile(""
			+ "public class MyClass {}", DynamicClassCompilerTest.class.getClassLoader()))
		.isInstanceOf(DynamicClassCompilerException.class);
	}

	@Test
	void testCompileCached() throws Exception {
		Class<?> clazz = dynamicClassCompiler.compile(""
			+ "package net.amygdalum.testrecorder.nopackage;"
			+ "public class MyClass {}", DynamicClassCompilerTest.class.getClassLoader());
		
		Class<?> cachedclazz = dynamicClassCompiler.compile(""
			+ "package net.amygdalum.testrecorder.nopackage;"
			+ "public class MyClass {}", DynamicClassCompilerTest.class.getClassLoader());
		
		assertThat(cachedclazz).isSameAs(clazz);
	}

}
