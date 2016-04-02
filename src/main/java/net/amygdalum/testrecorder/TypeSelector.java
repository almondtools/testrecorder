package net.amygdalum.testrecorder;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class TypeSelector {
	
	private TypeSelector() {
	}

	public static Stream<Class<?>> innerClasses(Class<?> of) {
		return Stream.of(of.getDeclaredClasses());
	}

	public static Predicate<Class<?>> startingWith(String... prefixes) {
		return cls -> Stream.of(prefixes).anyMatch(prefix -> cls.getSimpleName().startsWith(prefix));
	}

	public static Predicate<Class<?>> in(String... names) {
		return cls -> Stream.of(names).anyMatch(name -> cls.getSimpleName().equals(name));
	}

	public static Predicate<Class<?>> endingWith(String... suffixes) {
		return cls -> Stream.of(suffixes).anyMatch(suffix -> cls.getSimpleName().endsWith(suffix));
	}

}
