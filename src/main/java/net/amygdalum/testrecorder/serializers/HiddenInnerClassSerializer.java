package net.amygdalum.testrecorder.serializers;

import java.util.List;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.util.Types;

public abstract class HiddenInnerClassSerializer<T extends SerializedValue> implements Serializer<T> {

	private Class<?> clazz;
	protected SerializerFacade facade;

	public HiddenInnerClassSerializer(Class<?> clazz, SerializerFacade facade) {
		this.clazz = clazz;
		this.facade = facade;
	}

	public List<Class<?>> innerClasses() {
		return Types.innerClasses(clazz);
	}

}
