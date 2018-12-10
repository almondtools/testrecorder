package net.amygdalum.testrecorder.serializers;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.util.Types.isPrimitive;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedImmutable;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedProxy;

public class ProxySerializer extends AbstractCompositeSerializer implements Serializer<SerializedProxy> {

	public ProxySerializer() {
	}

	@Override
	public List<Class<?>> getMatchingClasses() {
		return emptyList();
	}

	@Override
	public Stream<?> components(Object object, SerializerSession session) {
		Builder<Object> components = Stream.builder();

		if (!session.facades(object)) {
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(object);
			components.add(invocationHandler);

			Class<?> objectClass = object.getClass();
			while (objectClass != Proxy.class && objectClass != Object.class && !session.excludes(objectClass)) {
				for (Field f : objectClass.getDeclaredFields()) {
					if (!session.excludes(f) && !session.facades(f)) {
						if (!isPrimitive(f.getType())) {
							components.add(fieldOf(object, f));
						}
					}
				}
				objectClass = objectClass.getSuperclass();
			}
		}

		return components.build();
	}

	@Override
	public SerializedProxy generate(Class<?> type, SerializerSession session) {
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

		if (session.facades(object)) {
			SerializedValue serializedInvocationHandler = new SerializedObject(InvocationHandler.class);
			serializedProxy.setInvocationHandler(serializedInvocationHandler);

			return;
		}

		InvocationHandler invocationHandler = Proxy.getInvocationHandler(object);
		SerializedValue serializedInvocationHandler = resolvedValueOf(session, InvocationHandler.class, invocationHandler);
		serializedProxy.setInvocationHandler(serializedInvocationHandler);

		Class<?> objectClass = object.getClass();
		while (objectClass != Proxy.class && objectClass != Object.class && !session.excludes(objectClass)) {
			for (Field f : objectClass.getDeclaredFields()) {
				if (!session.excludes(f)) {
					serializedProxy.addField(resolvedFieldOf(session, object, f));
				}
			}
			objectClass = objectClass.getSuperclass();
		}

	}

}
