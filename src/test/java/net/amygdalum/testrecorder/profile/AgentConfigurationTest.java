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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.util.ExtensibleClassLoader;
import net.amygdalum.testrecorder.util.LogLevel;
import net.amygdalum.testrecorder.util.LoggerExtension;
import net.amygdalum.testrecorder.util.TemporaryFolder;
import net.amygdalum.testrecorder.util.TemporaryFolderExtension;

@ExtendWith(TemporaryFolderExtension.class)
public class AgentConfigurationTest {

	private AgentConfiguration agentConfiguration;

	@BeforeEach
	public void before(TemporaryFolder folder) throws Exception {
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigNoArguments", "net.amygdalum.testrecorder.profile.DefaultConfigNoArguments\nnet.amygdalum.testrecorder.profile.OtherConfigNoArguments".getBytes());
		folder.provideFile("net.amygdalum.testrecorder.profile.ConfigWithArguments", "net.amygdalum.testrecorder.profile.DefaultConfigWithArguments".getBytes());

		agentConfiguration = new AgentConfiguration(folder.getRoot().toString());
	}

	@Test
	void testLoadConfiguration() throws Exception {
		ConfigNoArguments config = agentConfiguration.loadConfiguration(ConfigNoArguments.class);

		assertThat(config).isInstanceOf(DefaultConfigNoArguments.class);
	}

	@Test
	void testLoadConfigurationWithArguments() throws Exception {
		ConfigWithArguments loaded1 = agentConfiguration.loadConfiguration(ConfigWithArguments.class, "string");
		ConfigWithArguments loaded2 = agentConfiguration.loadConfiguration(ConfigWithArguments.class, "string");

		assertThat(loaded1).isInstanceOf(ConfigWithArguments.class);
		assertThat(loaded2).isInstanceOf(ConfigWithArguments.class);
		assertThat(loaded1).isNotSameAs(loaded2);
	}

	@Test
	void testLoadConfigurationIsolated() throws Exception {
		ConfigIsolated config = agentConfiguration.loadConfiguration(ConfigIsolated.class);

		assertThat(config).isNull();
	}

	@Test
	void testLoadConfigurationDefault() throws Exception {
		agentConfiguration.withDefaultValue(ConfigIsolated.class, () -> new ConfigIsolated() {
		});
		ConfigIsolated config = agentConfiguration.loadConfiguration(ConfigIsolated.class);

		assertThat(config).isNotNull();
	}

	@Test
	void testLoadCachedConfiguration() throws Exception {
		ConfigNoArguments loaded1 = agentConfiguration.loadConfiguration(ConfigNoArguments.class);
		ConfigNoArguments loaded2 = agentConfiguration.loadConfiguration(ConfigNoArguments.class);

		assertThat(loaded1).isSameAs(loaded2);
	}

	@Test
	void testLoadCachedClearedConfiguration() throws Exception {
		ConfigNoArguments loaded1 = agentConfiguration.loadConfiguration(ConfigNoArguments.class);
		agentConfiguration.reset();

		ConfigNoArguments loaded2 = agentConfiguration.loadConfiguration(ConfigNoArguments.class);

		assertThat(loaded1).isNotSameAs(loaded2);
	}

	@Test
	void testLoadConfigurations() throws Exception {
		List<ConfigNoArguments> configs = agentConfiguration.loadConfigurations(ConfigNoArguments.class);

		assertThat(configs).hasSize(2);
	}

	@Test
	void testLoadConfigurationsWithArguments() throws Exception {
		List<ConfigWithArguments> loaded1 = agentConfiguration.loadConfigurations(ConfigWithArguments.class, "string");
		List<ConfigWithArguments> loaded2 = agentConfiguration.loadConfigurations(ConfigWithArguments.class, "string");

		assertThat(loaded1).hasSize(1);
		assertThat(loaded2).hasSize(1);
		assertThat(loaded1).isNotSameAs(loaded2);
	}

	@Test
	void testLoadCachedConfigurations() throws Exception {
		List<ConfigNoArguments> loaded1 = agentConfiguration.loadConfigurations(ConfigNoArguments.class);
		List<ConfigNoArguments> loaded2 = agentConfiguration.loadConfigurations(ConfigNoArguments.class);

		assertThat(loaded1).isSameAs(loaded2);
	}

	@Test
	void testLoadCachedClearedConfigurations() throws Exception {
		List<ConfigNoArguments> loaded1 = agentConfiguration.loadConfigurations(ConfigNoArguments.class);
		agentConfiguration.reset();

		List<ConfigNoArguments> loaded2 = agentConfiguration.loadConfigurations(ConfigNoArguments.class);

		assertThat(loaded1).isNotSameAs(loaded2);
	}

	@ExtendWith(LoggerExtension.class)
	@ExtendWith(TemporaryFolderExtension.class)
	@Test
	public void testLoadWithBrokenFilesOnConfigPath(TemporaryFolder folder, @LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		AgentConfiguration agentConfiguration = new AgentConfiguration(folder.getRoot().toString()) {
			protected <T> java.util.stream.Stream<T> configsFrom(Path path, java.lang.Class<T> clazz, Object[] args) throws IOException {
				throw new IOException();
			};
		};

		agentConfiguration.load(ConfigNoArguments.class);

		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).matches("cannot load configuration file: .*ConfigNoArguments.*(\\n|\\r)*");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testLoadWithBrokenFilesOnClassPath(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ExtensibleClassLoader loader = new ExtensibleClassLoader(AgentConfigurationTest.class.getClassLoader());
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.profile.ConfigNoArguments", "net.amygdalum.testrecorder.profile.DefaultConfigNoArguments".getBytes());
		AgentConfiguration agentConfiguration = new AgentConfiguration(loader) {
			int count = 0;

			protected <T> Stream<T> configsFrom(Path path, Class<T> clazz, Object[] args) throws IOException {
				if (count == 0) {
					count++;
					throw new IOException();
				}
				return Stream.empty();
			};
		};

		agentConfiguration.load(ConfigNoArguments.class);

		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).matches("cannot load configuration file: .*ConfigNoArguments.*(\\n|\\r)*");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testLoadWithMissingFilesOnClassPath(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ExtensibleClassLoader loader = new ExtensibleClassLoader(AgentConfigurationTest.class.getClassLoader());
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.profile.ConfigNoArguments", "net.amygdalum.testrecorder.profile.DefaultConfigNoArguments".getBytes());
		AgentConfiguration agentConfiguration = new AgentConfiguration(loader) {
			int count = 0;

			protected <T> Stream<T> configsFrom(Path path, Class<T> clazz, Object[] args) throws IOException {
				if (count == 0) {
					count++;
					throw new NoSuchFileException(path.toString());
				}
				return Stream.empty();
			};
		};

		agentConfiguration.load(ConfigNoArguments.class);

		assertThat(info.toString()).matches("did not find configuration file.*(\\n|\\r)*");
		assertThat(error.toString()).isEmpty();
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testLoadWithSuccessOnClassPath(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ExtensibleClassLoader loader = new ExtensibleClassLoader(AgentConfigurationTest.class.getClassLoader());
		loader.defineResource("agentconfig/net.amygdalum.testrecorder.profile.ConfigNoArguments", "net.amygdalum.testrecorder.profile.DefaultConfigNoArguments".getBytes());
		AgentConfiguration agentConfiguration = new AgentConfiguration(loader);

		ConfigNoArguments loaded = agentConfiguration.load(ConfigNoArguments.class).findFirst().orElse(null);

		assertThat(loaded).isInstanceOf(DefaultConfigNoArguments.class);
		assertThat(error.toString()).isEmpty();
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testLoadWithBrokenFilesOnDefaultPath(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ExtensibleClassLoader loader = new ExtensibleClassLoader(AgentConfigurationTest.class.getClassLoader());
		AgentConfiguration agentConfiguration = new AgentConfiguration(loader) {
			protected <T> Stream<T> configsFrom(Path path, Class<T> clazz, Object[] args) throws IOException {
				throw new IOException();
			};
		};

		agentConfiguration.load(ConfigNoArguments.class);

		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).matches("cannot load configuration file: .*ConfigNoArguments.*(\\n|\\r)*");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testLoadWithMissingFilesOnDefaultPath(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ExtensibleClassLoader loader = new ExtensibleClassLoader(AgentConfigurationTest.class.getClassLoader());
		AgentConfiguration agentConfiguration = new AgentConfiguration(loader) {
			protected <T> Stream<T> configsFrom(Path path, Class<T> clazz, Object[] args) throws IOException {
				throw new NoSuchFileException(path.toString());
			};
		};

		agentConfiguration.load(ConfigNoArguments.class);

		assertThat(info.toString()).matches("did not find configuration file.*(\\n|\\r)*");
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
		AgentConfiguration agentConfiguration = new AgentConfiguration(loader);

		agentConfiguration.load(ConfigNoArguments.class);

		assertThat(error.toString()).contains("cannot load configuration from classpath");
	}

	@Test
	public void testConfigNoArgsFromWithSuccess() throws Exception {
		AgentConfiguration agentConfiguration = new AgentConfiguration();

		ConfigNoArguments config = agentConfiguration.configFrom("net.amygdalum.testrecorder.profile.DefaultConfigNoArguments", ConfigNoArguments.class, new Object[0]);

		assertThat(config).isInstanceOf(DefaultConfigNoArguments.class);
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testConfigWithArgsFromTooManyArgs(@LogLevel("error") ByteArrayOutputStream error) throws Exception {
		AgentConfiguration agentConfiguration = new AgentConfiguration();

		ConfigWithArguments config = agentConfiguration.configFrom("net.amygdalum.testrecorder.profile.DefaultConfigWithArguments", ConfigWithArguments.class, new Object[] { "arg1", "arg2" });

		assertThat(config).isNull();
		assertThat(error.toString()).contains("failed loading DefaultConfigWithArguments because no constructor matching (String, String)");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testConfigWithArgsFromTooFewArgs(@LogLevel("error") ByteArrayOutputStream error) throws Exception {
		AgentConfiguration agentConfiguration = new AgentConfiguration();

		ConfigWithArguments config = agentConfiguration.configFrom("net.amygdalum.testrecorder.profile.DefaultConfigWithArguments", ConfigWithArguments.class, new Object[0]);

		assertThat(config).isNull();
		assertThat(error.toString()).contains("failed loading DefaultConfigWithArguments because no constructor matching ()");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testConfigWithArgsFromMismatchingArgs(@LogLevel("error") ByteArrayOutputStream error) throws Exception {
		AgentConfiguration agentConfiguration = new AgentConfiguration();

		ConfigWithArguments config = agentConfiguration.configFrom("net.amygdalum.testrecorder.profile.DefaultConfigWithArguments", ConfigWithArguments.class, new Object[] { 1 });

		assertThat(config).isNull();
		assertThat(error.toString()).contains("failed loading DefaultConfigWithArguments because no constructor matching (Integer)");
	}

	@Test
	public void testConfigWithArgsFromWithSuccess() throws Exception {
		AgentConfiguration agentConfiguration = new AgentConfiguration();

		ConfigWithArguments config = agentConfiguration.configFrom("net.amygdalum.testrecorder.profile.DefaultConfigWithArguments", ConfigWithArguments.class, new Object[] { "arg" });

		assertThat(config).isInstanceOf(DefaultConfigWithArguments.class);
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testConfigNoArgsFromClassNotFound(@LogLevel("error") ByteArrayOutputStream error) throws Exception {
		AgentConfiguration agentConfiguration = new AgentConfiguration();

		ConfigNoArguments config = agentConfiguration.configFrom("net.amygdalum.testrecorder.profile.NotExisting", ConfigNoArguments.class, new Object[0]);

		assertThat(config).isNull();
		assertThat(error.toString()).contains("failed loading NotExisting from classpath");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testConfigNoArgsFromClassCastException(@LogLevel("error") ByteArrayOutputStream error) throws Exception {
		AgentConfiguration agentConfiguration = new AgentConfiguration();
		
		ConfigWithArguments config = agentConfiguration.configFrom("net.amygdalum.testrecorder.profile.DefaultConfigNoArguments", ConfigWithArguments.class, new Object[0]);
		
		assertThat(config).isNull();
		assertThat(error.toString()).contains("loaded class DefaultConfigNoArguments is not a subclass of ConfigWithArguments");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testConfigNoArgsFromOtherException(@LogLevel("error") ByteArrayOutputStream error) throws Exception {
		AgentConfiguration agentConfiguration = new AgentConfiguration();
		
		ConfigNoArguments config = agentConfiguration.configFrom("net.amygdalum.testrecorder.profile.BrokenConfigNoArguments", ConfigNoArguments.class, new Object[0]);
		
		assertThat(config).isNull();
		assertThat(error.toString()).contains("failed instantiating BrokenConfigNoArguments");
	}
	
}
