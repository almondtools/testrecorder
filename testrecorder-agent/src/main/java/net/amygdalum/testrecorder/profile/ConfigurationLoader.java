package net.amygdalum.testrecorder.profile;

import java.util.stream.Stream;

public interface ConfigurationLoader {

	<T> Stream<T> load(Class<T> clazz, Object... args);

}
