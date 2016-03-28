package net.amygdalum.testrecorder.serializers;

import java.util.function.Predicate;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;

public abstract class HiddenInnerClassSerializer<T extends SerializedValue> implements Serializer<T> {

	private Class<?> clazz;
	protected SerializerFacade facade;

	public HiddenInnerClassSerializer(Class<?> clazz, SerializerFacade facade) {
		this.clazz = clazz;
		this.facade = facade;
	}

	public Stream<Class<?>> innerClasses() {
		return Stream.of(clazz.getDeclaredClasses());
	}

	public Predicate<Class<?>> startingWith(String... prefixes) {
		return cls -> Stream.of(prefixes).anyMatch(prefix -> cls.getSimpleName().startsWith(prefix));
	}

	public Predicate<Class<?>> in(String... names) {
		return cls -> Stream.of(names).anyMatch(name -> cls.getSimpleName().equals(names));
	}

	public Predicate<Class<?>> endingWith(String... suffixes) {
		return cls -> Stream.of(suffixes).anyMatch(suffix -> cls.getSimpleName().endsWith(suffix));
	}

}
