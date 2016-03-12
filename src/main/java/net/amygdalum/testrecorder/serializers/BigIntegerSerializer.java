package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.List;

import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.SerializerFactory;
import net.amygdalum.testrecorder.values.SerializedBigInteger;

public class BigIntegerSerializer implements Serializer<SerializedBigInteger> {

	public BigIntegerSerializer(SerializerFacade facade) {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(BigInteger.class);
	}

	@Override
	public SerializedBigInteger generate(Type type, Class<?> valueType) {
		return new SerializedBigInteger(type, valueType);
	}

	@Override
	public void populate(SerializedBigInteger serializedObject, Object object) {
		serializedObject.setValue((BigInteger) object);
	}

	public static class Factory implements SerializerFactory<SerializedBigInteger> {

		@Override
		public BigIntegerSerializer newSerializer(SerializerFacade facade) {
			return new BigIntegerSerializer(facade);
		}

	}

}
