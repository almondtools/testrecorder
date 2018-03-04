package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.lang.reflect.Type;
import java.util.List;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.values.SerializedEnum;

public class EnumSerializer implements Serializer<SerializedEnum> {

	public EnumSerializer(SerializerFacade facade) {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return emptyList();
	}

	@Override
	public SerializedEnum generate(Type type) {
		while (!baseType(type).isEnum()) {
			type = baseType(type).getSuperclass();
		}
		return new SerializedEnum(type);
	}

	@Override
	public void populate(SerializedEnum serializedEnum, Object object) {
		Enum<?> e = (Enum<?>) object;
		serializedEnum.setName(e.name());
	}

}
