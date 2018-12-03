package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.util.ExtensibleClassLoader;
import net.amygdalum.testrecorder.util.LogLevel;
import net.amygdalum.testrecorder.util.LoggerExtension;
import net.amygdalum.testrecorder.util.TemporaryFolder;
import net.amygdalum.testrecorder.util.TemporaryFolderExtension;

@ExtendWith(TemporaryFolderExtension.class)
public class PathConfigurationLoaderTest {

	@Test
	void testLoad(TemporaryFolder folder) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArgumentsNonExclusive",
			"net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());

		PathConfigurationLoader loader = new PathConfigurationLoader(folder.getRoot());

		assertThat(loader.load(ConfigNoArgumentsNonExclusive.class).findFirst()).containsInstanceOf(DefaultConfigNoArguments.class);
	}

	@Test
	void testLoadWithClassLoader(TemporaryFolder folder) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArgumentsNonExclusive",
			"net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());

		ClassLoader classLoader = new ExtensibleClassLoader(PathConfigurationLoaderTest.class.getClassLoader(), "net.amygdalum.testrecorder.profile");
		PathConfigurationLoader loader = new PathConfigurationLoader(classLoader, folder.getRoot());

		Class<?> clazz = classLoader.loadClass(ConfigNoArgumentsNonExclusive.class.getName());
		assertThat(loader.load(clazz).findFirst())
			.map(config -> config.getClass().getClassLoader())
			.containsSame(classLoader);
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	void testLoadFileNotFound(TemporaryFolder folder, @LogLevel("debug") ByteArrayOutputStream debug) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArgumentsNonExclusive",
			"net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());

		PathConfigurationLoader loader = new PathConfigurationLoader(folder.getRoot()) {
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
	void testLoadIOException(TemporaryFolder folder, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArgumentsNonExclusive",
			"net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());

		PathConfigurationLoader loader = new PathConfigurationLoader(folder.getRoot()) {
			@Override
			protected <T> Stream<T> configsFrom(Path path, Class<T> clazz, Object[] args) throws IOException {
				throw new IOException();
			}
		};

		assertThat(loader.load(ConfigNoArgumentsNonExclusive.class).findFirst()).isNotPresent();
		assertThat(error.toString()).contains("cannot load configuration file");
	}

}
