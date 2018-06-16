package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.emptyList;
import static net.amygdalum.testrecorder.util.Types.baseType;

import java.util.List;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedEnum;

public class EnumSerializer implements Serializer<SerializedEnum> {

	public EnumSerializer() {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return emptyList();
	}

	@Override
	public Stream<?> components(Object object, SerializerSession session) {
		return Stream.empty();
	}

	@Override
	public SerializedEnum generate(Class<?> type, SerializerSession session) {
		while (!baseType(type).isEnum()) {
			type = baseType(type).getSuperclass();
		}
		return new SerializedEnum(type);
	}

	@Override
	public void populate(SerializedEnum serializedEnum, Object object, SerializerSession session) {
		Enum<?> e = (Enum<?>) object;
		serializedEnum.setName(e.name());
	}

}
