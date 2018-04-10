package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.List;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class BigIntegerSerializer implements Serializer<SerializedImmutable<BigInteger>> {

	public BigIntegerSerializer(SerializerFacade facade) {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(BigInteger.class);
	}

	@Override
	public SerializedImmutable<BigInteger> generate(Type type, SerializerSession session) {
		return new SerializedImmutable<>(type);
	}

	@Override
	public void populate(SerializedImmutable<BigInteger> serializedObject, Object object, SerializerSession session) {
		serializedObject.setValue((BigInteger) object);
	}

}
