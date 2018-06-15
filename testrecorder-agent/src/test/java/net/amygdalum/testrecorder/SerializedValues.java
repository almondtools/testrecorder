package net.amygdalum.testrecorder;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.List;

import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.serializers.BigIntegerSerializer;
import net.amygdalum.testrecorder.serializers.DefaultListSerializer;
import net.amygdalum.testrecorder.serializers.GenericSerializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.util.Types;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedObject;

public class SerializedValues {

	private ConfigurableSerializerFacade facade;
	private SerializerSession session;

	public SerializedValues(AgentConfiguration config) {
		facade = new ConfigurableSerializerFacade(config);
		session = facade.newSession();
	}

	public SerializedList list(Type type, List<?> values) {
		DefaultListSerializer serializer = new DefaultListSerializer(facade);
		SerializedList value = serializer.generate(Types.baseType(type), session);
		value.useAs(type);
		serializer.populate(value, values, session);
		return value;
	}

	public SerializedObject object(Type type, Object object) {
		GenericSerializer serializer = new GenericSerializer(facade);
		SerializedObject value = (SerializedObject) serializer.generate(object.getClass(), session);
		value.useAs(type);
		session.resolve(object, value);
		serializer.populate(value, object, session);
		return value;
	}

	public SerializedImmutable<BigInteger> bigInteger(BigInteger object) {
		BigIntegerSerializer serializer = new BigIntegerSerializer(facade);
		SerializedImmutable<BigInteger> value = serializer.generate(BigInteger.class, session);
		value.useAs(BigInteger.class);
		serializer.populate(value, object, session);
		return value;
	}

}
