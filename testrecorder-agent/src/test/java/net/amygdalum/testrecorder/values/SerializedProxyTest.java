package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.types.DeserializerContext.NULL;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedProxyTest {

	@Test
	public void testGetType() throws Exception {
		SerializedProxy value = new SerializedProxy(Proxy.class);
		value.setInterfaces(asList(new SerializedImmutable<Class<?>>(Class.class).withValue(MyInterface.class)));
		value.useAs(MyInterface.class);

		assertThat(value.getType()).isEqualTo(Proxy.class);
		assertThat(value.getUsedTypes()).contains(MyInterface.class);
	}

	@Test
	public void testAccept() throws Exception {
		SerializedProxy value = new SerializedProxy(Proxy.class);
		value.setInterfaces(asList(new SerializedImmutable<Class<?>>(Class.class).withValue(MyInterface.class)));

		assertThat(value.accept(new TestValueVisitor(), NULL)).isEqualTo("ReferenceType:SerializedProxy");
	}

	@Test
	public void testReferencedValues() throws Exception {
		SerializedProxy value = new SerializedProxy(Proxy.class);
		SerializedImmutable<Class<?>> interfaceValue = new SerializedImmutable<Class<?>>(Class.class).withValue(MyInterface.class);
		SerializedObject handlerValue = new SerializedObject(MyInvocationHandler.class);
		value.setInterfaces(asList(interfaceValue));
		value.setInvocationHandler(handlerValue);

		assertThat(value.referencedValues()).containsExactly(interfaceValue, handlerValue);
	}

	@Test
	public void testToString() throws Exception {
		SerializedProxy value = new SerializedProxy(Proxy.class);
		value.setInterfaces(asList(new SerializedImmutable<Class<?>>(Class.class).withValue(MyInterface.class)));

		assertThat(value.toString()).startsWith("proxy java.lang.reflect.Proxy");
	}

	public interface MyInterface {
		
	}
	
	public static class MyInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}
		
	}
}
