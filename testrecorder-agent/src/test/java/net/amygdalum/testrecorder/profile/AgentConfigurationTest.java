package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.util.ExtensibleClassLoader;
import net.amygdalum.testrecorder.util.LogLevel;
import net.amygdalum.testrecorder.util.LoggerExtension;
import net.amygdalum.testrecorder.util.TemporaryFolder;
import net.amygdalum.testrecorder.util.TemporaryFolderExtension;

@ExtendWith(TemporaryFolderExtension.class)
public class AgentConfigurationTest {

	@Test
	void testLoadConfiguration(TemporaryFolder folder) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArguments",
			"net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigWithArguments", "net.amygdalum.testrecorder.profile.DefaultConfigWithArguments".getBytes());

		AgentConfiguration agentConfiguration = new AgentConfiguration(new PathConfigurationLoader(folder.getRoot()));
		ConfigNoArguments config = agentConfiguration.loadConfiguration(ConfigNoArguments.class);

		assertThat(config).isInstanceOf(DefaultConfigNoArguments.class);
	}

	@Test
	void testLoadConfigurationWithArguments(TemporaryFolder folder) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArguments",
			"net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigWithArguments", "net.amygdalum.testrecorder.profile.DefaultConfigWithArguments".getBytes());

		AgentConfiguration agentConfiguration = new AgentConfiguration(new PathConfigurationLoader(folder.getRoot()));

		ConfigWithArguments loaded1 = agentConfiguration.loadConfiguration(ConfigWithArguments.class, "string");
		ConfigWithArguments loaded2 = agentConfiguration.loadConfiguration(ConfigWithArguments.class, "string");

		assertThat(loaded1).isInstanceOf(ConfigWithArguments.class);
		assertThat(loaded2).isInstanceOf(ConfigWithArguments.class);
		assertThat(loaded1).isNotSameAs(loaded2);
	}

	@Test
	void testLoadConfigurationIsolated(TemporaryFolder folder) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArguments",
			"net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigWithArguments", "net.amygdalum.testrecorder.profile.DefaultConfigWithArguments".getBytes());

		AgentConfiguration agentConfiguration = new AgentConfiguration(new PathConfigurationLoader(folder.getRoot()));

		ConfigIsolated config = agentConfiguration.loadConfiguration(ConfigIsolated.class);

		assertThat(config).isNull();
	}

	@Test
	void testLoadConfigurationDefault(TemporaryFolder folder) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArguments",
			"net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigWithArguments", "net.amygdalum.testrecorder.profile.DefaultConfigWithArguments".getBytes());

		AgentConfiguration agentConfiguration = new AgentConfiguration(new PathConfigurationLoader(folder.getRoot()));

		agentConfiguration.withDefaultValue(ConfigIsolated.class, () -> new ConfigIsolated() {
		});
		ConfigIsolated config = agentConfiguration.loadConfiguration(ConfigIsolated.class);

		assertThat(config).isNotNull();
	}

	@Test
	void testLoadCachedConfiguration(TemporaryFolder folder) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArguments",
			"net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigWithArguments", "net.amygdalum.testrecorder.profile.DefaultConfigWithArguments".getBytes());

		AgentConfiguration agentConfiguration = new AgentConfiguration(new PathConfigurationLoader(folder.getRoot()));

		ConfigNoArguments loaded1 = agentConfiguration.loadConfiguration(ConfigNoArguments.class);
		ConfigNoArguments loaded2 = agentConfiguration.loadConfiguration(ConfigNoArguments.class);

		assertThat(loaded1).isSameAs(loaded2);
	}

	@Test
	void testLoadCachedClearedConfiguration(TemporaryFolder folder) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArguments",
			"net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigWithArguments", "net.amygdalum.testrecorder.profile.DefaultConfigWithArguments".getBytes());

		AgentConfiguration agentConfiguration = new AgentConfiguration(new PathConfigurationLoader(folder.getRoot()));

		ConfigNoArguments loaded1 = agentConfiguration.loadConfiguration(ConfigNoArguments.class);
		agentConfiguration.reset();

		ConfigNoArguments loaded2 = agentConfiguration.loadConfiguration(ConfigNoArguments.class);

		assertThat(loaded1).isNotSameAs(loaded2);
	}

	@Test
	void testLoadConfigurations(TemporaryFolder folder) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArguments",
			"net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigWithArguments", "net.amygdalum.testrecorder.profile.DefaultConfigWithArguments".getBytes());

		AgentConfiguration agentConfiguration = new AgentConfiguration(new PathConfigurationLoader(folder.getRoot()));

		List<ConfigNoArguments> configs = agentConfiguration.loadConfigurations(ConfigNoArguments.class);

		assertThat(configs).hasSize(2);
	}

	@Test
	void testLoadConfigurationsWithArguments(TemporaryFolder folder) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArguments",
			"net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigWithArguments", "net.amygdalum.testrecorder.profile.DefaultConfigWithArguments".getBytes());

		AgentConfiguration agentConfiguration = new AgentConfiguration(new PathConfigurationLoader(folder.getRoot()));

		List<ConfigWithArguments> loaded1 = agentConfiguration.loadConfigurations(ConfigWithArguments.class, "string");
		List<ConfigWithArguments> loaded2 = agentConfiguration.loadConfigurations(ConfigWithArguments.class, "string");

		assertThat(loaded1).hasSize(1);
		assertThat(loaded2).hasSize(1);
		assertThat(loaded1).isNotSameAs(loaded2);
	}

	@Test
	void testLoadCachedConfigurations(TemporaryFolder folder) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArguments",
			"net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigWithArguments", "net.amygdalum.testrecorder.profile.DefaultConfigWithArguments".getBytes());

		AgentConfiguration agentConfiguration = new AgentConfiguration(new PathConfigurationLoader(folder.getRoot()));

		List<ConfigNoArguments> loaded1 = agentConfiguration.loadConfigurations(ConfigNoArguments.class);
		List<ConfigNoArguments> loaded2 = agentConfiguration.loadConfigurations(ConfigNoArguments.class);

		assertThat(loaded1).isSameAs(loaded2);
	}

	@Test
	void testLoadCachedClearedConfigurations(TemporaryFolder folder) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArguments",
			"net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigWithArguments", "net.amygdalum.testrecorder.profile.DefaultConfigWithArguments".getBytes());

		AgentConfiguration agentConfiguration = new AgentConfiguration(new PathConfigurationLoader(folder.getRoot()));

		List<ConfigNoArguments> loaded1 = agentConfiguration.loadConfigurations(ConfigNoArguments.class);
		agentConfiguration.reset();

		List<ConfigNoArguments> loaded2 = agentConfiguration.loadConfigurations(ConfigNoArguments.class);

		assertThat(loaded1).isNotSameAs(loaded2);
	}

	@ExtendWith(LoggerExtension.class)
	@ExtendWith(TemporaryFolderExtension.class)
	@Test
	public void testLoadWithBrokenFilesOnConfigPath(TemporaryFolder folder, @LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		AgentConfiguration agentConfiguration = new AgentConfiguration(new PathConfigurationLoader(folder.getRoot()) {
			protected <T> java.util.stream.Stream<T> configsFrom(Path path, java.lang.Class<T> clazz, Object[] args) throws IOException {
				throw new IOException();
			};
		});

		agentConfiguration.load(ConfigNoArguments.class);

		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).matches("cannot load configuration file: .*ConfigNoArguments.*(\\n|\\r)*");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testLoadWithBrokenFilesOnClassPath(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ExtensibleClassLoader loader = new ExtensibleClassLoader(AgentConfigurationTest.class.getClassLoader());
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.profile.ConfigNoArguments", "net.amygdalum.testrecorder.profile.DefaultConfigNoArguments".getBytes());
		AgentConfiguration agentConfiguration = new AgentConfiguration(new ClassPathConfigurationLoader(loader) {

			protected <T> Stream<T> configsFrom(Path path, Class<T> clazz, Object[] args) throws IOException {
				throw new IOException();
			};
		});

		agentConfiguration.load(ConfigNoArguments.class);

		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).matches("cannot load configuration file: .*ConfigNoArguments.*(\\n|\\r)*");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testLoadWithMissingFilesOnClassPath(@LogLevel("debug") ByteArrayOutputStream debug, @LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error)
		throws Exception {
		ExtensibleClassLoader loader = new ExtensibleClassLoader(AgentConfigurationTest.class.getClassLoader());
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.profile.ConfigNoArguments", "net.amygdalum.testrecorder.profile.DefaultConfigNoArguments".getBytes());
		AgentConfiguration agentConfiguration = new AgentConfiguration(new ClassPathConfigurationLoader(loader) {

			protected <T> Stream<T> configsFrom(Path path, Class<T> clazz, Object[] args) throws IOException {
				throw new NoSuchFileException(path.toString());
			};
		});

		agentConfiguration.load(ConfigNoArguments.class);

		assertThat(debug.toString()).matches("did not find configuration file.*(\\n|\\r)*");
		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).isEmpty();
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testLoadWithSuccessOnClassPath(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ExtensibleClassLoader loader = new ExtensibleClassLoader(AgentConfigurationTest.class.getClassLoader());
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.profile.ConfigNoArguments", "net.amygdalum.testrecorder.profile.DefaultConfigNoArguments".getBytes());
		AgentConfiguration agentConfiguration = new AgentConfiguration(new ClassPathConfigurationLoader(loader), new DefaultPathConfigurationLoader(loader));

		ConfigNoArguments loaded = agentConfiguration.load(ConfigNoArguments.class).findFirst().orElse(null);

		assertThat(loaded).isInstanceOf(DefaultConfigNoArguments.class);
		assertThat(info.toString()).contains("loading DefaultConfigNoArguments");
		assertThat(error.toString()).isEmpty();
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testLoadWithBrokenFilesOnDefaultPath(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ExtensibleClassLoader loader = new ExtensibleClassLoader(AgentConfigurationTest.class.getClassLoader());
		AgentConfiguration agentConfiguration = new AgentConfiguration(new DefaultPathConfigurationLoader(loader) {
			protected <T> Stream<T> configsFrom(Path path, Class<T> clazz, Object[] args) throws IOException {
				throw new IOException();
			};
		});

		agentConfiguration.load(ConfigNoArguments.class);

		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).matches("cannot load configuration file: .*ConfigNoArguments.*(\\n|\\r)*");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testLoadWithMissingFilesOnDefaultPath(@LogLevel("debug") ByteArrayOutputStream debug, @LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error)
		throws Exception {
		ExtensibleClassLoader loader = new ExtensibleClassLoader(AgentConfigurationTest.class.getClassLoader());
		AgentConfiguration agentConfiguration = new AgentConfiguration(new DefaultPathConfigurationLoader(loader) {
			protected <T> Stream<T> configsFrom(Path path, Class<T> clazz, Object[] args) throws IOException {
				throw new NoSuchFileException(path.toString());
			};
		});

		agentConfiguration.load(ConfigNoArguments.class);

		assertThat(debug.toString()).matches("did not find configuration file.*(\\n|\\r)*");
		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).isEmpty();
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testLoadWithBrokenClassLoader(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ExtensibleClassLoader loader = new ExtensibleClassLoader(AgentConfigurationTest.class.getClassLoader()) {
			@Override
			public Enumeration<URL> getResources(String name) throws IOException {
				throw new IOException();
			}
		};
		AgentConfiguration agentConfiguration = new AgentConfiguration(new ClassPathConfigurationLoader(loader));

		agentConfiguration.load(ConfigNoArguments.class);

		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).contains("cannot load configuration from classpath");
	}

}
