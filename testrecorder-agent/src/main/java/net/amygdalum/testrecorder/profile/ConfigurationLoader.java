package net.amygdalum.testrecorder.profile;

import static net.amygdalum.testrecorder.util.Types.boxedType;

import java.lang.reflect.Constructor;
import java.util.stream.Stream;

public interface ConfigurationLoader {

	<T> Stream<T> load(Class<T> clazz, Object... args);

	public static ClassLoader defaultClassLoader(Class<?> clazz) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = clazz.getClassLoader();
		}
		if (loader == null) {
			loader = ClassLoader.getSystemClassLoader();
		}
		return loader;
	}

	public default boolean matches(Constructor<?> constructor, Object[] args) {
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
