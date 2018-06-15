package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.util.ContextClassloaderExtension;
import net.amygdalum.testrecorder.util.ExtensibleClassLoader;

@ExtendWith(ContextClassloaderExtension.class)
public class ConfigurationLoaderTest {

	@Test
	void testDefaultClassLoaderWithContextLoader() throws Exception {
		ExtensibleClassLoader loader = new ExtensibleClassLoader(ClassLoader.getSystemClassLoader());
		System.out.println(Thread.currentThread());
		Thread.currentThread().setContextClassLoader(loader);

		assertThat(ConfigurationLoader.defaultClassLoader(String.class)).isSameAs(loader);
	}

	@Test
	void testDefaultClassLoaderWithoutContextLoader() throws Exception {
		System.out.println(Thread.currentThread());
		Thread.currentThread().setContextClassLoader(null);
		
		assertThat(ConfigurationLoader.defaultClassLoader(String.class)).isSameAs(ClassLoader.getSystemClassLoader());
	}
	
	@Test
	void testDefaultClassLoaderWithoutContextLoaderButClassLoader() throws Exception {
		System.out.println(Thread.currentThread());
		Thread.currentThread().setContextClassLoader(null);
		
		assertThat(ConfigurationLoader.defaultClassLoader(ConfigurationLoaderTest.class)).isSameAs(ConfigurationLoaderTest.class.getClassLoader());
	}
	
}
