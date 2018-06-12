package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

public class DefaultPathConfigurationLoaderTest {

	@Test
	void testDefaultPathConfigurationLoader() throws Exception {
		Object result = new Object();
		DefaultPathConfigurationLoader loader = new DefaultPathConfigurationLoader() {
			@SuppressWarnings("unchecked")
			@Override
			protected <T> Stream<T> configsFrom(Path path, Class<T> clazz, Object[] args) throws IOException {
				assertThat(path.getParent()).isEqualTo(Paths.get("agentconfig"));
				return (Stream<T>) Stream.of(result);
			}
		};
		assertThat(loader.load(Object.class).findFirst()).contains(result);
	}

}
