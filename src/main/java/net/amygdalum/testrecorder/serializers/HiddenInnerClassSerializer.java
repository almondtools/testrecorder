package net.amygdalum.testrecorder.serializers;

import java.util.stream.Stream;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.TypeSelector;

public abstract class HiddenInnerClassSerializer<T extends SerializedValue> implements Serializer<T> {

	private Class<?> clazz;
	protected SerializerFacade facade;

	public HiddenInnerClassSerializer(Class<?> clazz, SerializerFacade facade) {
		this.clazz = clazz;
		this.facade = facade;
	}

	public Stream<Class<?>> innerClasses() {
		return TypeSelector.innerClasses(clazz);
	}

}
