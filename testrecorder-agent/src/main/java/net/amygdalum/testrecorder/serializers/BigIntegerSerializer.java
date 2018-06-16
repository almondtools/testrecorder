package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class BigIntegerSerializer implements Serializer<SerializedImmutable<BigInteger>> {

	public BigIntegerSerializer() {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(BigInteger.class);
	}

	@Override
	public Stream<?> components(Object object, SerializerSession session) {
		return Stream.empty();
	}

	@Override
	public SerializedImmutable<BigInteger> generate(Class<?> type, SerializerSession session) {
		return new SerializedImmutable<>(type);
	}

	@Override
	public void populate(SerializedImmutable<BigInteger> serializedObject, Object object, SerializerSession session) {
		serializedObject.setValue((BigInteger) object);
	}

}
