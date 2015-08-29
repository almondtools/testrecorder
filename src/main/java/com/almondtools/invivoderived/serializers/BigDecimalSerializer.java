package com.almondtools.invivoderived.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import com.almondtools.invivoderived.Serializer;
import com.almondtools.invivoderived.SerializerFacade;
import com.almondtools.invivoderived.SerializerFactory;
import com.almondtools.invivoderived.values.SerializedBigDecimal;

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
