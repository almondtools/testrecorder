package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.util.ExtensibleClassLoader;
import net.amygdalum.testrecorder.util.LogLevel;
import net.amygdalum.testrecorder.util.LoggerExtension;

public class ClassPathConfigurationLoaderTest {

	@Nested
	class testLoad {
		@Test
		void common() throws Exception {
			ExtensibleClassLoader classLoader = new ExtensibleClassLoader(ClassPathConfigurationLoaderTest.class.getClassLoader());
			classLoader.defineResource("agentconfig/net.amygdalum.testrecorder.profile.ConfigNoArgumentsNonExclusive", "net.amygdalum.testrecorder.profile.DefaultConfigNoArguments".getBytes());

			ClassPathConfigurationLoader loader = new ClassPathConfigurationLoader(classLoader);

			assertThat(loader.load(ConfigNoArgumentsNonExclusive.class).findFirst()).containsInstanceOf(DefaultConfigNoArguments.class);
		}

		@ExtendWith(LoggerExtension.class)
		@Test
		void withClassLoaderError(@LogLevel("error") ByteArrayOutputStream error) throws Exception {
			ExtensibleClassLoader classLoader = new ExtensibleClassLoader(ClassPathConfigurationLoaderTest.class.getClassLoader()) {
				@Override
				public Enumeration<URL> getResources(String name) throws IOException {
					throw new IOException();
				}
			};
			classLoader.defineResource("agentconfig/net.amygdalum.testrecorder.profile.ConfigNoArgumentsNonExclusive", "net.amygdalum.testrecorder.profile.DefaultConfigNoArguments".getBytes());

			ClassPathConfigurationLoader loader = new ClassPathConfigurationLoader(classLoader);

			assertThat(loader.load(ConfigNoArgumentsNonExclusive.class).findFirst()).isNotPresent();
			assertThat(error.toString()).contains("cannot load configuration from classpath");
		}

		@ExtendWith(LoggerExtension.class)
		@Test
		void withFileNotFound(@LogLevel("debug") ByteArrayOutputStream debug) throws Exception {
			ExtensibleClassLoader classLoader = new ExtensibleClassLoader(ClassPathConfigurationLoaderTest.class.getClassLoader());
			classLoader.defineResource("agentconfig/net.amygdalum.testrecorder.profile.ConfigNoArgumentsNonExclusive", "net.amygdalum.testrecorder.profile.DefaultConfigNoArguments".getBytes());

			ClassPathConfigurationLoader loader = new ClassPathConfigurationLoader(classLoader) {
				@Override
				protected <T> Stream<T> configsFrom(Path path, Class<T> clazz, Object[] args) throws IOException {
					throw new FileNotFoundException();
				}
			};

			assertThat(loader.load(ConfigNoArgumentsNonExclusive.class).findFirst()).isNotPresent();
			assertThat(debug.toString()).contains("did not find configuration file");
		}

		@ExtendWith(LoggerExtension.class)
		@Test
		void withIOException(@LogLevel("error") ByteArrayOutputStream error) throws Exception {
			ExtensibleClassLoader classLoader = new ExtensibleClassLoader(ClassPathConfigurationLoaderTest.class.getClassLoader());
			classLoader.defineResource("agentconfig/net.amygdalum.testrecorder.profile.ConfigNoArgumentsNonExclusive", "net.amygdalum.testrecorder.profile.DefaultConfigNoArguments".getBytes());

			ClassPathConfigurationLoader loader = new ClassPathConfigurationLoader(classLoader) {
				@Override
				protected <T> Stream<T> configsFrom(Path path, Class<T> clazz, Object[] args) throws IOException {
					throw new IOException();
				}
			};

			assertThat(loader.load(ConfigNoArgumentsNonExclusive.class).findFirst()).isNotPresent();
			assertThat(error.toString()).contains("cannot load configuration file");
		}
	}
}
