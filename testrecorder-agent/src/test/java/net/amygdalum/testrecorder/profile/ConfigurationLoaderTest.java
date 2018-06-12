package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.util.ExtensibleClassLoader;

public class ConfigurationLoaderTest {

	@Test
	public void testDefaultClassLoaderWithContextLoader() throws Exception {
		ExtensibleClassLoader loader = new ExtensibleClassLoader(ClassLoader.getSystemClassLoader());
		Thread.currentThread().setContextClassLoader(loader);

		assertThat(ConfigurationLoader.defaultClassLoader(String.class)).isSameAs(loader);
	}

	@Test
	public void testDefaultClassLoaderWithoutContextLoader() throws Exception {
		Thread.currentThread().setContextClassLoader(null);
		
		assertThat(ConfigurationLoader.defaultClassLoader(String.class)).isSameAs(ClassLoader.getSystemClassLoader());
	}
	
	@Test
	public void testDefaultClassLoaderWithoutContextLoaderButClassLoader() throws Exception {
		Thread.currentThread().setContextClassLoader(null);
		
		assertThat(ConfigurationLoader.defaultClassLoader(ConfigurationLoaderTest.class)).isSameAs(ConfigurationLoaderTest.class.getClassLoader());
	}
	
}
