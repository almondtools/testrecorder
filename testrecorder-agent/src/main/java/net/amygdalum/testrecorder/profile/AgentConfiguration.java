package net.amygdalum.testrecorder.profile;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import net.amygdalum.testrecorder.util.Logger;

public class AgentConfiguration {

	private List<ConfigurationLoader> configurationLoaders;
	private Map<Class<?>, Supplier<?>> defaultValues;
	private Map<Class<?>, Object> singleValues;
	private Map<Class<?>, List<?>> multiValues;


	public AgentConfiguration(ConfigurationLoader... configurationLoaders) {
		this(asList(configurationLoaders));
	}
	
	public AgentConfiguration(List<ConfigurationLoader> configurationLoaders) {
		this.configurationLoaders = configurationLoaders;
		this.defaultValues = new IdentityHashMap<>();
		this.singleValues = new IdentityHashMap<>();
		this.multiValues = new IdentityHashMap<>();
	}
	
	public List<ConfigurationLoader> getConfigurationLoaders() {
		return configurationLoaders;
	}
	
	public void setConfigurationLoaders(List<ConfigurationLoader> configurationLoaders) {
		this.configurationLoaders = configurationLoaders;
	}

	public AgentConfiguration reset() {
		singleValues.clear();
		multiValues.clear();
		return this;
	}

	public <T> AgentConfiguration withDefaultValue(Class<T> clazz, Supplier<T> defaultValue) {
		defaultValues.put(clazz, defaultValue);
		return this;
	}

	public <T> Optional<T> loadOptionalConfiguration(Class<T> clazz, Object... args) {
		if (args.length == 0) {
			Object cached = singleValues.get(clazz);
			if (cached != null) {
				return Optional.of(clazz.cast(cached));
			}
		}
		Optional<T> config = load(clazz, args).findFirst();
		if (args.length == 0 && config.isPresent()) {
			singleValues.put(clazz, config.get());
		}
		return config;
	}

	public <T> T loadConfiguration(Class<T> clazz, Object... args) {
		if (args.length == 0) {
			Object cached = singleValues.get(clazz);
			if (cached != null) {
				return clazz.cast(cached);
			}
		}
		Optional<T> matchingConfig = load(clazz, args).findFirst();
		T config = matchingConfig.orElseGet(() -> loadDefault(clazz));
		if (args.length == 0) {
			singleValues.put(clazz, config);
		}
		return config;
	}

	protected <T> T loadDefault(Class<T> clazz) {
		Supplier<?> defaultConfigSupplier = defaultValues.get(clazz);
		if (defaultConfigSupplier == null) {
			return null;
		}
		T defaultConfig = clazz.cast(defaultConfigSupplier.get());
		logLoad("default", defaultConfig);
		return defaultConfig;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> loadConfigurations(Class<T> clazz, Object... args) {
		if (args.length == 0) {
			List<?> cached = multiValues.get(clazz);
			if (cached != null) {
				return (List<T>) cached;
			}
		}
		List<T> configs = load(clazz, args).collect(toList());
		if (args.length == 0) {
			multiValues.put(clazz, configs);
		}
		return configs;
	}

	protected <T> Stream<T> load(Class<T> clazz, Object... args) {
		Builder<T> configurations = Stream.<T> builder();

		for (ConfigurationLoader configurationLoader : configurationLoaders) {
			configurationLoader.load(clazz, args)
				.forEach(configurations::add);
		}

		return configurations.build().distinct();
	}

	private <T> T logLoad(String prefix, T object) {
		if (object != null) {
			Logger.info("loading " + prefix + " " + object.getClass().getSimpleName());
		}
		return object;
	}

}
