package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedProxy;

public class ProxySerializer implements Serializer<SerializedProxy> {

	private SerializerFacade facade;

	public ProxySerializer(SerializerFacade facade) {
		this.facade = facade;
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return emptyList();
	}

	@Override
	public SerializedProxy generate(Type type, SerializerSession session) {
		return new SerializedProxy(type);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void populate(SerializedProxy serializedProxy, Object object, SerializerSession session) {
		Class<?>[] interfaces = object.getClass().getInterfaces();
		List<SerializedImmutable<Class<?>>> serializedInterfaces = Arrays.stream(interfaces)
			.map(interfaceClass -> (SerializedImmutable<Class<?>>) (SerializedImmutable) new SerializedImmutable<>(Class.class).withValue(interfaceClass))
			.collect(toList());
		serializedProxy.setInterfaces(serializedInterfaces);

		InvocationHandler invocationHandler = Proxy.getInvocationHandler(object);
		if (session.facades(object)) {
			serializedProxy.setInvocationHandler(facade.serializePlaceholder(InvocationHandler.class, invocationHandler, session));
		} else {
			SerializedValue serializedInvocationHandler = facade.serialize(InvocationHandler.class, invocationHandler, session);
			serializedProxy.setInvocationHandler(serializedInvocationHandler);

			Class<?> objectClass = object.getClass();
			while (objectClass != Proxy.class && objectClass != Object.class && !session.excludes(objectClass)) {
				for (Field f : objectClass.getDeclaredFields()) {
					if (!session.excludes(f)) {
						serializedProxy.addField(facade.serialize(f, object, session));
					}
				}
				objectClass = objectClass.getSuperclass();
			}
		}
	}

}
