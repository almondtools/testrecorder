package net.amygdalum.testrecorder.dynamiccompile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.ExtensibleClassLoader;

public class RenderedTestTest {

	@Test
	void testRenderedTestDefaultConstructor() throws Exception {
		ClassLoader classLoader = RenderedTestTest.class.getClassLoader();
		RenderedTest renderedTest = new RenderedTest(classLoader, "mycode");

		assertThat(renderedTest.getTestClassLoader()).isSameAs(classLoader);
		assertThat(renderedTest.getTestCode()).isEqualTo("mycode");
	}

	@Test
	void testRenderedTestConvenienceConstructor() throws Exception {
		ExtensibleClassLoader loader = new ExtensibleClassLoader(RenderedTestTest.class.getClassLoader());
		loader.redefineClass("net.amygdalum.testrecorder.dynamiccompile.RenderedTestTest");
		RenderedTest renderedTest = new RenderedTest(loader.loadClass("net.amygdalum.testrecorder.dynamiccompile.RenderedTestTest"), "mycode");

		assertThat(renderedTest.getTestClassLoader()).isSameAs(loader);
		assertThat(renderedTest.getTestCode()).isEqualTo("mycode");
	}

	@Test
	void testRenderedTestNullConstructor() throws Exception {
		RenderedTest renderedTest = new RenderedTest((Class<?>) null, "mycode");

		assertThat(renderedTest.getTestClassLoader()).isSameAs(RenderedTestTest.class.getClassLoader());
		assertThat(renderedTest.getTestCode()).isEqualTo("mycode");
	}
}
