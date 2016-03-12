package net.amygdalum.testrecorder.serializers;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import net.amygdalum.testrecorder.Serializer;
import net.amygdalum.testrecorder.SerializerFacade;
import net.amygdalum.testrecorder.SerializerFactory;
import net.amygdalum.testrecorder.values.SerializedBigDecimal;

public class BigDecimalSerializer implements Serializer<SerializedBigDecimal> {

	public BigDecimalSerializer(SerializerFacade facade) {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return asList(BigDecimal.class);
	}

	@Override
	public SerializedBigDecimal generate(Type type, Class<?> valueType) {
		return new SerializedBigDecimal(type, valueType);
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
