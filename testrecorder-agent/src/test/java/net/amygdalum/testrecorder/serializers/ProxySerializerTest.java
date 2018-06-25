package net.amygdalum.testrecorder.serializers;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.Serializer;
import net.amygdalum.testrecorder.types.SerializerSession;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedProxy;

public class ProxySerializerTest {

	private SerializerSession session;
	private Serializer<SerializedProxy> serializer;

	@BeforeEach
	void before() throws Exception {
		session = mock(SerializerSession.class);
		serializer = new ProxySerializer();
	}

	@Test
	void testGetMatchingClasses() throws Exception {
		assertThat(serializer.getMatchingClasses()).isEmpty();
	}

	@Test
	void testGenerate() throws Exception {
		SerializedProxy value = serializer.generate(Proxy.class, session);
		value.useAs(parameterized(Callable.class, null, String.class));

		assertThat(value.getUsedTypes()).containsExactly(parameterized(Callable.class, null, String.class));
		assertThat(value.getType()).isEqualTo(Proxy.class);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testComponents() throws Exception {
		Object o = new Object();
		Callable<String> p = (Callable<String>) Proxy.newProxyInstance(o.getClass().getClassLoader(), new Class<?>[] { Callable.class }, new CallableInvocationHandler());

		assertThat(p.call()).isEqualTo("result");
		assertThat(serializer.components(p, session)).anyMatch(element -> element instanceof CallableInvocationHandler);
	}

	@SuppressWarnings("unchecked")
	@Test
	void testPopulate() throws Exception {
		Object o = new Object();
		CallableInvocationHandler h = new CallableInvocationHandler();
		Callable<String> p = (Callable<String>) Proxy.newProxyInstance(o.getClass().getClassLoader(), new Class<?>[] { Callable.class }, h);
		SerializedProxy serializedObject = new SerializedProxy(Proxy.class);

		SerializedObject serializedH = new SerializedObject(InvocationHandler.class);
		SerializedObject serializedMethod = new SerializedObject(Method.class);

		when(session.ref(h, InvocationHandler.class)).thenReturn(serializedH);
		when(session.ref(isA(Method.class), eq(Method.class))).thenReturn(serializedMethod);
		serializer.populate(serializedObject, p, session);

		assertThat(serializedObject.getInvocationHandler()).isEqualTo(serializedH);
		assertThat(serializedObject.getFields()).allMatch(field -> field.getValue().getType() == Method.class);
	}

	public static class CallableInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return "result";
		}

	}
}
