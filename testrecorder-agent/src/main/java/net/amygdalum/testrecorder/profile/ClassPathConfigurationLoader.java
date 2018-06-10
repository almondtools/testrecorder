package net.amygdalum.testrecorder.profile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import net.amygdalum.testrecorder.util.Logger;

public class ClassPathConfigurationLoader extends AbstractPathConfigurationLoader implements ConfigurationLoader {

	public ClassPathConfigurationLoader() {
	}

	public ClassPathConfigurationLoader(ClassLoader loader) {
		super(loader);
	}
	
	@Override
	public <T> Stream<T> load(Class<T> clazz, Object... args) {
		Builder<T> configurations = Stream.<T> builder();

		try {
			Enumeration<URL> urls = loader.getResources("agentconfig/" + clazz.getName());
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				Path lookupPath = pathFrom(url);
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
		} catch (IOException e) {
			Logger.error("cannot load configuration from classpath", e);
		}
		
		return configurations.build();
	}

}
