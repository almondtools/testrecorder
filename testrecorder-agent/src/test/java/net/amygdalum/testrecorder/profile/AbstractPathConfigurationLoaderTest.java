package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.util.LogLevel;
import net.amygdalum.testrecorder.util.LoggerExtension;
import net.amygdalum.testrecorder.util.TemporaryFolderExtension;

@ExtendWith(TemporaryFolderExtension.class)
public class AbstractPathConfigurationLoaderTest {

	@Test
	public void testConfigNoArgsFromWithSuccess() throws Exception {
		ConfigNoArguments config = loader().configFrom("net.amygdalum.testrecorder.profile.DefaultConfigNoArguments", ConfigNoArguments.class, new Object[0]);

		assertThat(config).isInstanceOf(DefaultConfigNoArguments.class);
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testConfigWithArgsFromTooManyArgs(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ConfigWithArguments config = loader().configFrom("net.amygdalum.testrecorder.profile.DefaultConfigWithArguments", ConfigWithArguments.class, new Object[] { "arg1", "arg2" });

		assertThat(config).isNull();
		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).contains("failed loading DefaultConfigWithArguments because no constructor matching (String, String)");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testConfigWithArgsFromTooFewArgs(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ConfigWithArguments config = loader().configFrom("net.amygdalum.testrecorder.profile.DefaultConfigWithArguments", ConfigWithArguments.class, new Object[0]);

		assertThat(config).isNull();
		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).contains("failed loading DefaultConfigWithArguments because no constructor matching ()");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testConfigWithArgsFromMismatchingArgs(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ConfigWithArguments config = loader().configFrom("net.amygdalum.testrecorder.profile.DefaultConfigWithArguments", ConfigWithArguments.class, new Object[] { 1 });

		assertThat(config).isNull();
		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).contains("failed loading DefaultConfigWithArguments because no constructor matching (Integer)");
	}

	@Test
	public void testConfigWithArgsFromWithSuccess() throws Exception {
		ConfigWithArguments config = loader().configFrom("net.amygdalum.testrecorder.profile.DefaultConfigWithArguments", ConfigWithArguments.class, new Object[] { "arg" });

		assertThat(config).isInstanceOf(DefaultConfigWithArguments.class);
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testConfigNoArgsFromClassNotFound(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ConfigNoArguments config = loader().configFrom("net.amygdalum.testrecorder.profile.NotExisting", ConfigNoArguments.class, new Object[0]);

		assertThat(config).isNull();
		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).contains("failed loading NotExisting from classpath");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testConfigNoArgsFromClassCastException(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ConfigWithArguments config = loader().configFrom("net.amygdalum.testrecorder.profile.DefaultConfigNoArguments", ConfigWithArguments.class, new Object[0]);

		assertThat(config).isNull();
		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).contains("loaded class DefaultConfigNoArguments is not a subclass of ConfigWithArguments");
	}

	@ExtendWith(LoggerExtension.class)
	@Test
	public void testConfigNoArgsFromOtherException(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
		ConfigNoArguments config = loader().configFrom("net.amygdalum.testrecorder.profile.BrokenConfigNoArguments", ConfigNoArguments.class, new Object[0]);

		assertThat(config).isNull();
		assertThat(info.toString()).isEmpty();
		assertThat(error.toString()).contains("failed instantiating BrokenConfigNoArguments");
	}

	private AbstractPathConfigurationLoader loader() {
		return new AbstractPathConfigurationLoader(AbstractPathConfigurationLoaderTest.class.getClassLoader()) {

			@Override
			public <T> Stream<T> load(Class<T> clazz, Object... args) {
				return Stream.empty();
			}
		};
	}

}
