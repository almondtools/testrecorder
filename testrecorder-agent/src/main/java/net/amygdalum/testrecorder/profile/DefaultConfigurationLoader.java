package net.amygdalum.testrecorder.profile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import net.amygdalum.testrecorder.util.Logger;

public class DefaultConfigurationLoader extends AbstractPathConfigurationLoader implements ConfigurationLoader {

	public DefaultConfigurationLoader() {
	}

	public DefaultConfigurationLoader(ClassLoader loader) {
		super(loader);
	}
	
	public static Path defaultConfigPath() {
		return Paths.get("agentconfig");
	}

	@Override
	public <T> Stream<T> load(Class<T> clazz, Object... args) {
		Builder<T> configurations = Stream.<T> builder();

		Path lookupPath = defaultConfigPath().resolve(clazz.getName());
		try {
			configsFrom(lookupPath, clazz, args)
				.map(this::logLoad)
				.forEach(configurations::add);
		} catch (FileNotFoundException | NoSuchFileException e) {
			Logger.debug("did not find configuration file " + lookupPath + ", skipping");
		} catch (IOException e) {
			Logger.error("cannot load configuration file: " + lookupPath);
		}
		
		return configurations.build();
	}

}
