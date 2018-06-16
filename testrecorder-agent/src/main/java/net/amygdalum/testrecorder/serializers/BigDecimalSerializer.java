package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class BigDecimalSerializer implements Serializer<SerializedImmutable<BigDecimal>> {

	public BigDecimalSerializer() {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(BigDecimal.class);
	}

	@Override
	public Stream<?> components(Object object, SerializerSession session) {
		return Stream.empty();
	}

	@Override
	public SerializedImmutable<BigDecimal> generate(Class<?> type, SerializerSession session) {
		return new SerializedImmutable<>(type);
	}

	@Override
	public void populate(SerializedImmutable<BigDecimal> serializedObject, Object object, SerializerSession session) {
		serializedObject.setValue((BigDecimal) object);
	}

}
