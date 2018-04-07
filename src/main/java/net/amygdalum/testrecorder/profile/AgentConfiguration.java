package net.amygdalum.testrecorder.profile;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.Types.boxedType;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import net.amygdalum.testrecorder.util.Logger;

public class AgentConfiguration {

	private ClassLoader loader;
	private List<Path> configurationPaths;
	private Map<Class<?>, Supplier<?>> defaultValues;
	private Map<Class<?>, Object> singleValues;
	private Map<Class<?>, List<?>> multiValues;

	public AgentConfiguration(String... configurationPaths) {
		this(defaultClassLoader(), configurationPaths);
	}

	public AgentConfiguration(ClassLoader loader, String... configurationPaths) {
		this.loader = loader;
		this.configurationPaths = pathsOf(configurationPaths);
		this.defaultValues = new IdentityHashMap<>();
		this.singleValues = new IdentityHashMap<>();
		this.multiValues = new IdentityHashMap<>();
	}

	private static ClassLoader defaultClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = AgentConfiguration.class.getClassLoader();
		}
		if (loader == null) {
			loader = ClassLoader.getSystemClassLoader();
		}
		return loader;
	}

	private static List<Path> pathsOf(String[] paths) {
		return Arrays.stream(paths)
			.map(path -> Paths.get(path))
			.collect(toList());
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

	public void setLoader(ClassLoader loader) {
		this.loader = loader;
	}

	public <T> T loadConfiguration(Class<T> clazz, Object... args) {
		if (args.length == 0) {
			Object cached = singleValues.get(clazz);
			if (cached != null) {
				return clazz.cast(cached);
			}
		}
		Optional<T> matchingConfig = load(clazz, args).findFirst();
		T config = matchingConfig.isPresent() ? matchingConfig.get() : loadDefault(clazz);
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

		for (Path configurationPath : configurationPaths) {
			Path lookupPath = configurationPath.resolve(clazz.getName());
			try {
				configsFrom(lookupPath, clazz, args)
					.map(this::logLoad)
					.forEach(configurations::add);
			} catch (FileNotFoundException | NoSuchFileException e) {
				Logger.info("did not find configuration file " + lookupPath + ", skipping");
			} catch (IOException e) {
				Logger.error("cannot load configuration file: " + lookupPath);
			}
		}

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
					Logger.info("did not find configuration file " + lookupPath + ", skipping");
				} catch (IOException e) {
					Logger.error("cannot load configuration file: " + lookupPath);
				}
			}
		} catch (IOException e) {
			Logger.error("cannot load configuration from classpath", e);
		}

		if (configurationPaths.isEmpty()) {
			Path lookupPath = defaultConfigPath().resolve(clazz.getName());
			try {
				configsFrom(lookupPath, clazz, args)
					.map(this::logLoad)
					.forEach(configurations::add);
			} catch (FileNotFoundException | NoSuchFileException e) {
				Logger.info("did not find configuration file " + lookupPath + ", skipping");
			} catch (IOException e) {
				Logger.error("cannot load configuration file: " + lookupPath);
			}
		}

		return configurations.build().distinct();
	}

	protected Path defaultConfigPath() {
		return Paths.get("agentconfig");
	}

	private Path pathFrom(URL url) throws IOException {
		try {
			URI uri = url.toURI();
			try {
				return Paths.get(uri);
			} catch (FileSystemNotFoundException e) {
				FileSystems.newFileSystem(uri, Collections.singletonMap("create", "true"));
				return Paths.get(uri);
			}
		} catch (URISyntaxException e) {
			throw new IOException(e);
		}
	}

	private <T> T logLoad(T object) {
		if (object != null) {
			Logger.info("loading " + object.getClass().getSimpleName());
		}
		return object;
	}

	private <T> T logLoad(String prefix, T object) {
		if (object != null) {
			Logger.info("loading " + prefix + " " + object.getClass().getSimpleName());
		}
		return object;
	}

	protected <T> Stream<T> configsFrom(Path path, Class<T> clazz, Object[] args) throws IOException {
		return new BufferedReader(new InputStreamReader(Files.newInputStream(path), UTF_8)).lines()
			.map(line -> line.trim())
			.filter(line -> !line.isEmpty())
			.map(name -> configFrom(name, clazz, args))
			.filter(Objects::nonNull);

	}

	protected <T> T configFrom(String name, Class<T> superClazz, Object[] args) {
		try {
			Class<?> clazz = loader.loadClass(name);
			Optional<Constructor<?>> constructor = Arrays.stream(clazz.getConstructors())
				.filter(c -> matches(c, args))
				.findFirst();
			if (constructor.isPresent()) {
				return superClazz.cast(constructor.get().newInstance(args));
			} else {
				Logger.error("failed loading " + clazz.getSimpleName() + " because no constructor matching "
					+ Arrays.stream(args).map(arg -> arg == null ? "null" : arg.getClass().getSimpleName()).collect(joining(", ", "(", ")"))
					+ ", skipping");
				return null;
			}
		} catch (ClassNotFoundException e) {
			int pos = name.lastIndexOf('.');
			String simpleName = name.substring(pos + 1);
			Logger.error("failed loading " + simpleName + " from classpath, skipping");
			return null;
		} catch (ClassCastException e) {
			int pos = name.lastIndexOf('.');
			String simpleName = name.substring(pos + 1);
			Logger.error("loaded class " + simpleName + " is not a subclass of " + superClazz.getSimpleName() + ", skipping");
			return null;
		} catch (InvocationTargetException | InstantiationException | IllegalArgumentException | IllegalAccessException e) {
			int pos = name.lastIndexOf('.');
			String simpleName = name.substring(pos + 1);
			Logger.error("failed instantiating " + simpleName + ": ", e);
			return null;
		}
	}

	private boolean matches(Constructor<?> constructor, Object[] args) {
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		if (parameterTypes.length != args.length) {
			return false;
		}
		for (int i = 0; i < parameterTypes.length; i++) {
			if (args[i] != null && !boxedType(parameterTypes[i]).isInstance(args[i])) {
				return false;
			}
		}
		return true;
	}

}
