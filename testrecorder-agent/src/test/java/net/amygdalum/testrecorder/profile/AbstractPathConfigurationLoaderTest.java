package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import net.amygdalum.testrecorder.util.LogLevel;
import net.amygdalum.testrecorder.util.LoggerExtension;
import net.amygdalum.testrecorder.util.TemporaryFolderExtension;

@ExtendWith(TemporaryFolderExtension.class)
public class AbstractPathConfigurationLoaderTest {

	@Nested
	class testConfigFrom {
		@Test
		public void noArgsFromWithSuccess() throws Exception {
			ConfigNoArgumentsNonExclusive config = loader().configFrom("net.amygdalum.testrecorder.profile.DefaultConfigNoArguments", ConfigNoArgumentsNonExclusive.class, new Object[0]);

			assertThat(config).isInstanceOf(DefaultConfigNoArguments.class);
		}

		@ExtendWith(LoggerExtension.class)
		@Test
		public void withArgsFromTooManyArgs(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
			ConfigWithArgumentsNonExclusive config = loader().configFrom("net.amygdalum.testrecorder.profile.DefaultConfigWithArguments", ConfigWithArgumentsNonExclusive.class, new Object[] {"arg1", "arg2"});

			assertThat(config).isNull();
			assertThat(info.toString()).isEmpty();
			assertThat(error.toString()).contains("failed loading net.amygdalum.testrecorder.profile.DefaultConfigWithArguments because no constructor matching (String, String)");
		}

		@ExtendWith(LoggerExtension.class)
		@Test
		public void withArgsFromTooFewArgs(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
			ConfigWithArgumentsNonExclusive config = loader().configFrom("net.amygdalum.testrecorder.profile.DefaultConfigWithArguments", ConfigWithArgumentsNonExclusive.class, new Object[0]);

			assertThat(config).isNull();
			assertThat(info.toString()).isEmpty();
			assertThat(error.toString()).contains("failed loading net.amygdalum.testrecorder.profile.DefaultConfigWithArguments because no constructor matching ()");
		}

		@ExtendWith(LoggerExtension.class)
		@Test
		public void withArgsFromMismatchingArgs(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
			ConfigWithArgumentsNonExclusive config = loader().configFrom("net.amygdalum.testrecorder.profile.DefaultConfigWithArguments", ConfigWithArgumentsNonExclusive.class, new Object[] {1});

			assertThat(config).isNull();
			assertThat(info.toString()).isEmpty();
			assertThat(error.toString()).contains("failed loading net.amygdalum.testrecorder.profile.DefaultConfigWithArguments because no constructor matching (Integer)");
		}

		@Test
		public void withArgsFromWithSuccess() throws Exception {
			ConfigWithArgumentsNonExclusive config = loader().configFrom("net.amygdalum.testrecorder.profile.DefaultConfigWithArguments", ConfigWithArgumentsNonExclusive.class, new Object[] {"arg"});

			assertThat(config).isInstanceOf(DefaultConfigWithArguments.class);
		}

		@ExtendWith(LoggerExtension.class)
		@Test
		public void noArgsFromClassNotFound(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
			ConfigNoArgumentsNonExclusive config = loader().configFrom("net.amygdalum.testrecorder.profile.NotExisting", ConfigNoArgumentsNonExclusive.class, new Object[0]);

			assertThat(config).isNull();
			assertThat(info.toString()).isEmpty();
			assertThat(error.toString()).contains("failed loading net.amygdalum.testrecorder.profile.NotExisting from classpath");
		}

		@ExtendWith(LoggerExtension.class)
		@Test
		public void noArgsFromClassCastException(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
			ConfigWithArgumentsNonExclusive config = loader().configFrom("net.amygdalum.testrecorder.profile.DefaultConfigNoArguments", ConfigWithArgumentsNonExclusive.class, new Object[0]);

			assertThat(config).isNull();
			assertThat(info.toString()).isEmpty();
			assertThat(error.toString()).contains("loaded class net.amygdalum.testrecorder.profile.DefaultConfigNoArguments is not a subclass of ConfigWithArguments");
		}

		@ExtendWith(LoggerExtension.class)
		@Test
		public void noArgsFromOtherException(@LogLevel("info") ByteArrayOutputStream info, @LogLevel("error") ByteArrayOutputStream error) throws Exception {
			ConfigNoArgumentsNonExclusive config = loader().configFrom("net.amygdalum.testrecorder.profile.BrokenConfigNoArguments", ConfigNoArgumentsNonExclusive.class, new Object[0]);

			assertThat(config).isNull();
			assertThat(info.toString()).isEmpty();
			assertThat(error.toString()).contains("failed instantiating net.amygdalum.testrecorder.profile.BrokenConfigNoArguments");
		}
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
