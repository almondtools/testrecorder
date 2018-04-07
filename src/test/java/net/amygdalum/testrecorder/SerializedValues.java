package net.amygdalum.testrecorder;

import static net.amygdalum.xrayinterface.XRayInterface.xray;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import net.amygdalum.testrecorder.ConfigurableSerializerFacade;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.serializers.BigIntegerSerializer;
import net.amygdalum.testrecorder.serializers.DefaultListSerializer;
import net.amygdalum.testrecorder.serializers.GenericSerializer;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedObject;

public class SerializedValues {

	private ConfigurableSerializerFacade facade;

	public SerializedValues(AgentConfiguration config) {
		facade = new ConfigurableSerializerFacade(config);
	}

	public SerializedList list(Type type, List<?> values) {
		DefaultListSerializer serializer = new DefaultListSerializer(facade);
		SerializedList value = serializer.generate(type);
		value.useAs(type);
		serializer.populate(value, values);
		return value;
	}

	public SerializedObject object(Type type, Object object) {
		GenericSerializer serializer = new GenericSerializer(facade);
		SerializedObject value = (SerializedObject) serializer.generate(object.getClass());
		value.useAs(type);
		xray(facade).to(OpenFacade.class).getSerialized().put(object, value);
		serializer.populate(value, object);
		return value;
	}

	public SerializedImmutable<BigInteger> bigInteger(BigInteger object) {
		BigIntegerSerializer serializer = new BigIntegerSerializer(facade);
		SerializedImmutable<BigInteger> value = serializer.generate(BigInteger.class);
		value.useAs(BigInteger.class);
		serializer.populate(value, object);
		return value;
	}

	interface OpenFacade {
		Map<Object, SerializedValue> getSerialized();
	}

}
