package net.amygdalum.testrecorder.profile;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.profile.ConfigurationLoader.defaultClassLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import net.amygdalum.testrecorder.util.Logger;

public class PathConfigurationLoader extends AbstractPathConfigurationLoader implements ConfigurationLoader {

	private List<Path> configurationPaths;

	public PathConfigurationLoader(Path... configurationPaths) {
		this(defaultClassLoader(PathConfigurationLoader.class), asList(configurationPaths));
	}

	public PathConfigurationLoader(List<Path> configurationPaths) {
		this(defaultClassLoader(PathConfigurationLoader.class), configurationPaths);
	}

	public PathConfigurationLoader(ClassLoader loader, Path... configurationPaths) {
		this(loader, asList(configurationPaths));
	}

	public PathConfigurationLoader(ClassLoader loader, List<Path> configurationPaths) {
		super(loader);
		this.configurationPaths = configurationPaths;
	}

	@Override
	public <T> Stream<T> load(Class<T> clazz, Object... args) {
		Builder<T> configurations = Stream.<T> builder();

		for (Path configurationPath : configurationPaths) {
			Path lookupPath = configurationPath.resolve(clazz.getName());
			try {
				configsFrom(lookupPath, clazz, args)
					.map(this::logLoad)
					.forEach(configurations::add);
			} catch (FileNotFoundException | NoSuchFileException e) {
				Logger.debug("did not find configuration file " + lookupPath + ", skipping");
			} catch (IOException e) {
				Logger.error("cannot load configuration file: " + lookupPath);
			}
		}

		return configurations.build();
	}

}
