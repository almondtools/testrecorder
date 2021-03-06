package net.amygdalum.testrecorder.dynamiccompile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.generator.RenderedTest;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;

public class RenderedTestTest {

	@Nested
	class testRenderedTest {
		@Test
		void withDefaultConstructor() throws Exception {
			ClassLoader classLoader = RenderedTestTest.class.getClassLoader();
			RenderedTest renderedTest = new RenderedTest(classLoader, "mycode");

			assertThat(renderedTest.getTestClassLoader()).isSameAs(classLoader);
			assertThat(renderedTest.getTestCode()).isEqualTo("mycode");
			assertThat(renderedTest.toString()).isEqualTo("mycode");
		}

		@Test
		void withConvenienceConstructor() throws Exception {
			ExtensibleClassLoader loader = new ExtensibleClassLoader(RenderedTestTest.class.getClassLoader());
			loader.redefineClass("net.amygdalum.testrecorder.dynamiccompile.RenderedTestTest");
			RenderedTest renderedTest = new RenderedTest(loader.loadClass("net.amygdalum.testrecorder.dynamiccompile.RenderedTestTest"), "mycode");

			assertThat(renderedTest.getTestClassLoader()).isSameAs(loader);
			assertThat(renderedTest.getTestCode()).isEqualTo("mycode");
		}

		@Test
		void withNullConstructor() throws Exception {
			RenderedTest renderedTest = new RenderedTest((Class<?>) null, "mycode");

			assertThat(renderedTest.getTestClassLoader()).isSameAs(RenderedTestTest.class.getClassLoader());
			assertThat(renderedTest.getTestCode()).isEqualTo("mycode");
		}
	}
}
