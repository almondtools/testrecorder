package net.amygdalum.testrecorder.profile;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static net.amygdalum.testrecorder.util.Types.boxedType;

import java.io.BufferedReader;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.util.Logger;

public abstract class AbstractPathConfigurationLoader implements ConfigurationLoader {

	protected ClassLoader loader;

	public AbstractPathConfigurationLoader() {
		this(defaultClassLoader());
	}

	public AbstractPathConfigurationLoader(ClassLoader loader) {
		this.loader = loader;
	}

	protected <T> T logLoad(T object) {
		if (object != null) {
			Logger.info("loading " + object.getClass().getSimpleName());
		}
		return object;
	}

	protected boolean matches(Constructor<?> constructor, Object[] args) {
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

	protected Path pathFrom(URL url) throws IOException {
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

	protected static ClassLoader defaultClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = AgentConfiguration.class.getClassLoader();
		}
		if (loader == null) {
			loader = ClassLoader.getSystemClassLoader();
		}
		return loader;
	}

}
