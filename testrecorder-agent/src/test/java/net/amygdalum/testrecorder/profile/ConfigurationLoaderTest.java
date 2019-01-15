package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.util.ContextClassloaderExtension;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;

@ExtendWith(ContextClassloaderExtension.class)
public class ConfigurationLoaderTest {

	@Nested
	class testDefaultClassLoader {
		@Test
		void withContextLoader() throws Exception {
			ExtensibleClassLoader loader = new ExtensibleClassLoader(ClassLoader.getSystemClassLoader());
			System.out.println(Thread.currentThread());
			Thread.currentThread().setContextClassLoader(loader);

			assertThat(ConfigurationLoader.defaultClassLoader(String.class)).isSameAs(loader);
		}

		@Test
		void withoutContextLoader() throws Exception {
			System.out.println(Thread.currentThread());
			Thread.currentThread().setContextClassLoader(null);

			assertThat(ConfigurationLoader.defaultClassLoader(String.class)).isSameAs(ClassLoader.getSystemClassLoader());
		}

		@Test
		void withoutContextLoaderButClassLoader() throws Exception {
			System.out.println(Thread.currentThread());
			Thread.currentThread().setContextClassLoader(null);

			assertThat(ConfigurationLoader.defaultClassLoader(ConfigurationLoaderTest.class)).isSameAs(ConfigurationLoaderTest.class.getClassLoader());
		}
	}
}
