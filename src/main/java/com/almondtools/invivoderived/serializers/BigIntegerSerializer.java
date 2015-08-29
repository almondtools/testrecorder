package com.almondtools.invivoderived.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.List;

import com.almondtools.invivoderived.Serializer;
import com.almondtools.invivoderived.SerializerFacade;
import com.almondtools.invivoderived.SerializerFactory;
import com.almondtools.invivoderived.values.SerializedBigInteger;

public class BigIntegerSerializer implements Serializer<SerializedBigInteger> {

	public BigIntegerSerializer(SerializerFacade facade) {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(BigInteger.class);
	}

	@Override
	public SerializedBigInteger generate(Type type) {
		return new SerializedBigInteger(type);
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
