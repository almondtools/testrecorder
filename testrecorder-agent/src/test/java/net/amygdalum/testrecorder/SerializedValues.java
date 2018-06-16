package net.amygdalum.testrecorder;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.List;

import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.serializers.SerializerFacade;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.util.Types;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;

public class SerializedValues {

	private SerializerFacade facade;
	private SerializerSession session;

	public SerializedValues(AgentConfiguration config) {
		this(new ConfigurableSerializerFacade(config));
	}

	public SerializedValues(SerializerFacade facade) {
		this.facade = facade;
		this.session = facade.newSession();
	}

	public SerializedList list(Type type, Object... values) {
		return list(type, asList(values));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SerializedList list(Type type, List<?> values) {
		try {
			Class<?> baseType = Types.baseType(type);
			List list = (List) baseType.newInstance();
			list.addAll(values);
			return (SerializedList) facade.serialize(type, list, session);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public SerializedObject object(Type type, Object object) {
		return (SerializedObject) facade.serialize(type, object, session);
	}

	@SuppressWarnings("unchecked")
	public SerializedImmutable<BigInteger> bigInteger(BigInteger object) {
		return (SerializedImmutable<BigInteger>) facade.serialize(BigInteger.class, object, session);
	}

	public static SerializedNull nullValue(Class<?> type) {
		SerializedNull nullInstance = nullInstance();
		if (type != null) {
			nullInstance.useAs(type);
		}
		return nullInstance;
	}

}
