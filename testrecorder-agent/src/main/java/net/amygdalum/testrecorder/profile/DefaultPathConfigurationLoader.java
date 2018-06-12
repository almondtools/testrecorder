package net.amygdalum.testrecorder.profile;

import static net.amygdalum.testrecorder.profile.ConfigurationLoader.defaultClassLoader;

import java.nio.file.Paths;

public class DefaultPathConfigurationLoader extends PathConfigurationLoader implements ConfigurationLoader {

	public DefaultPathConfigurationLoader() {
		this(defaultClassLoader(DefaultPathConfigurationLoader.class));
	}

	public DefaultPathConfigurationLoader(ClassLoader loader) {
		super(loader, Paths.get("agentconfig"));
	}
	
}
