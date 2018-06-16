package net.amygdalum.testrecorder.serializers;

import java.util.List;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.util.Types;

public abstract class HiddenInnerClassSerializer<T extends SerializedValue> extends AbstractCompositeSerializer implements Serializer<T> {

	private Class<?> clazz;

	public HiddenInnerClassSerializer(Class<?> clazz) {
		this.clazz = clazz;
	}

	public List<Class<?>> innerClasses() {
		return Types.innerClasses(clazz);
	}

}
