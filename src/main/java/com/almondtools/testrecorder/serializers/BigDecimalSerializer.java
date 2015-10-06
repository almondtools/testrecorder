package com.almondtools.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import com.almondtools.testrecorder.Serializer;
import com.almondtools.testrecorder.SerializerFacade;
import com.almondtools.testrecorder.SerializerFactory;
import com.almondtools.testrecorder.values.SerializedBigDecimal;

public class BigDecimalSerializer implements Serializer<SerializedBigDecimal> {

	public BigDecimalSerializer(SerializerFacade facade) {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(BigDecimal.class);
	}

	@Override
	public SerializedBigDecimal generate(Type type) {
		return new SerializedBigDecimal(type);
	}

	@Override
	public void populate(SerializedBigDecimal serializedObject, Object object) {
		serializedObject.setValue((BigDecimal) object);
	}

	public static class Factory implements SerializerFactory<SerializedBigDecimal> {

		@Override
		public BigDecimalSerializer newSerializer(SerializerFacade facade) {
			return new BigDecimalSerializer(facade);
		}

	}

}
