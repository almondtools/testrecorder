package net.amygdalum.testrecorder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.Types.boxedType;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import net.amygdalum.testrecorder.util.Logger;

public class AgentConfiguration {

	private ClassLoader loader;
	private List<Path> configurationPaths;

	public AgentConfiguration(ClassLoader loader, String... configurationPaths) {
		this.loader = loader;
		this.configurationPaths = pathsOf(configurationPaths);
	}

	private static List<Path> pathsOf(String[] paths) {
		return Arrays.stream(paths)
			.map(path -> Paths.get(path))
			.collect(toList());
	}

	public <T> T loadDefaultConfiguration(T defaultInstance) {
		Logger.info("loading default " + defaultInstance.getClass().getSimpleName());
		return defaultInstance;
	}

	public <T> Optional<T> loadConfiguration(Class<T> clazz, Object... args) {
		for (Path configurationPath : configurationPaths) {
			Path lookupPath = configurationPath.resolve(clazz.getName());
			try {
				InputStream configResource = Files.newInputStream(lookupPath);
				Optional<T> configs = configsFrom(configResource, clazz, args).findFirst();
				if (configs.isPresent()) {
					return configs.map(this::logLoad);
				}
			} catch (FileNotFoundException | NoSuchFileException e) {
				Logger.info("did not find configuration file " + lookupPath + ", continuing");
			} catch (IOException e) {
				Logger.error("cannot load configuration file: " + lookupPath);
			}
		}

		InputStream classPathResource = loader.getResourceAsStream("META-INF/services/" + clazz.getName());
		if (classPathResource != null) {
			Optional<T> configs = configsFrom(classPathResource, clazz, args).findFirst();
			if (configs.isPresent()) {
				return configs.map(this::logLoad);
			}
		}

		if (configurationPaths.isEmpty()) {
			Path defaultResourcePath = Paths.get("agentconfig");
			Path lookupPath = defaultResourcePath.resolve(clazz.getName());
			try {
				InputStream configResource = Files.newInputStream(lookupPath);
				Optional<T> configs = configsFrom(configResource, clazz, args).findFirst();
				if (configs.isPresent()) {
					return configs.map(this::logLoad);
				}
			} catch (FileNotFoundException | NoSuchFileException e) {
				Logger.info("did not find configuration file " + lookupPath + ", continuing");
			} catch (IOException e) {
				Logger.error("cannot load configuration file: " + lookupPath);
			}
		}
		return Optional.empty();
	}

	private <T> T logLoad(T object) {
		Logger.info("loading " + object.getClass().getSimpleName());
		return object;
	}

	public <T> List<T> loadConfigurations(Class<T> clazz, Object... args) {
		Builder<T> configurations = Stream.<T> builder();

		for (Path configurationPath : configurationPaths) {
			Path lookupPath = configurationPath.resolve(clazz.getName());
			try {
				InputStream configResource = Files.newInputStream(lookupPath);
				configsFrom(configResource, clazz, args)
					.map(this::logLoad)
					.forEach(configurations::add);
			} catch (FileNotFoundException | NoSuchFileException e) {
				Logger.info("did not find configuration file " + lookupPath + ", continuing");
			} catch (IOException e) {
				Logger.error("cannot load configuration file: " + lookupPath);
			}
		}

		InputStream classPathResource = loader.getResourceAsStream("META-INF/services/" + clazz.getName());
		if (classPathResource != null) {
			configsFrom(classPathResource, clazz, args)
				.map(this::logLoad)
				.forEach(configurations::add);
		}

		if (configurationPaths.isEmpty()) {
			Path defaultResourcePath = Paths.get("agentconfig");
			Path lookupPath = defaultResourcePath.resolve(clazz.getName());
			try {
				InputStream configResource = Files.newInputStream(lookupPath);
				configsFrom(configResource, clazz, args)
					.map(this::logLoad)
					.forEach(configurations::add);
			} catch (FileNotFoundException | NoSuchFileException e) {
				Logger.info("did not find configuration file " + lookupPath + ", continuing");
			} catch (IOException e) {
				Logger.error("cannot load configuration file: " + lookupPath);
			}
		}

		return configurations.build().distinct().collect(toList());
	}

	private <T> Stream<T> configsFrom(InputStream resource, Class<T> clazz, Object[] args) {
		return new BufferedReader(new InputStreamReader(resource, UTF_8)).lines()
			.map(line -> line.trim())
			.map(name -> configFrom(name, clazz, args))
			.filter(Objects::nonNull);

	}

	private <T> T configFrom(String name, Class<T> superClazz, Object[] args) {
		try {
			Class<?> clazz = loader.loadClass(name);
			Optional<Constructor<?>> constructor = Arrays.stream(clazz.getConstructors())
				.filter(c -> matches(c, args))
				.findFirst();
			if (constructor.isPresent()) {
				return superClazz.cast(constructor.get().newInstance(args));
			} else {
				Logger.error("failed loading " + clazz.getSimpleName() + " because no constructor matching "
					+ Arrays.stream(args).map(arg -> arg == null ? "null" : arg.getClass().getSimpleName()).collect(joining(",","(",")"))
					+", skipping");
				return null;
			}
		} catch (ClassNotFoundException e) {
			int pos = name.lastIndexOf('.');
			String simpleName = name.substring(pos + 1);
			Logger.error("failed loading " + simpleName + " from class path, skipping");
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
