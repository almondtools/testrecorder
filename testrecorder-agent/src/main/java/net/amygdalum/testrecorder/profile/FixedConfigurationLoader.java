package net.amygdalum.testrecorder.profile;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.util.Logger;

public class FixedConfigurationLoader implements ConfigurationLoader {

	private Map<Class<?>, List<Function<Object[], ?>>> fixedConfigs;

	public FixedConfigurationLoader() {
		this.fixedConfigs = new HashMap<>();
	}
	
	public FixedConfigurationLoader provide (Class<?> clazz, Object value) {
		fixedConfigs.computeIfAbsent(clazz, key -> new ArrayList<>()).add(args -> value);
		return this;
	}

	public FixedConfigurationLoader provide (Class<?> clazz, Function<Object[], ?> provider) {
		fixedConfigs.computeIfAbsent(clazz, key -> new ArrayList<>()).add(provider);
		return this;
	}

	@Override
	public <T> Stream<T> load(Class<T> clazz, Object... args) {
		return fixedConfigs.computeIfAbsent(clazz, key -> emptyList()).stream()
			.map(config -> load(clazz, config, args))
			.filter(Objects::nonNull);
	}

	private <T> T load(Class<T> clazz, Function<Object[], ?> config, Object... args) {
		try {
			Object result = config.apply(args);
			try {
				return clazz.cast(result);
			} catch (ClassCastException e) {
				String simpleName = result.getClass().getSimpleName();
				Logger.error("loaded class " + simpleName + " is not a subclass of " + clazz.getSimpleName() + ", skipping");
				return null;
			}
		} catch (RuntimeException e) {
			Logger.error("failed to provide " + clazz.getSimpleName() + ": ", e);
			return null;
		}
	}

}
