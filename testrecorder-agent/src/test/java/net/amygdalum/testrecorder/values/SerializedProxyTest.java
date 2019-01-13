package net.amygdalum.testrecorder.values;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.FieldSignature;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedProxyTest {

	@Test
	void testGetType() throws Exception {
		SerializedProxy value = new SerializedProxy(Proxy.class);
		value.setInterfaces(asList(new SerializedImmutable<Class<?>>(Class.class).withValue(MyInterface.class)));
		value.useAs(MyInterface.class);

		assertThat(value.getType()).isEqualTo(Proxy.class);
	}

	@Test
	void testGetUsedTypes() throws Exception {
		SerializedProxy value = new SerializedProxy(Proxy.class);
		value.setInterfaces(asList(new SerializedImmutable<Class<?>>(Class.class).withValue(MyInterface.class)));
		value.useAs(MyInterface.class);

		assertThat(value.getUsedTypes()).contains(MyInterface.class);
	}

	@Test
	void testAccept() throws Exception {
		SerializedProxy value = new SerializedProxy(Proxy.class);
		value.setInterfaces(asList(new SerializedImmutable<Class<?>>(Class.class).withValue(MyInterface.class)));

		assertThat(value.accept(new TestValueVisitor())).isEqualTo("ReferenceType:SerializedProxy");
	}

	@Test
	void testReferencedValues() throws Exception {
		SerializedProxy value = new SerializedProxy(Proxy.class);
		SerializedImmutable<Class<?>> interfaceValue = new SerializedImmutable<Class<?>>(Class.class).withValue(MyInterface.class);
		SerializedObject handlerValue = new SerializedObject(MyInvocationHandler.class);
		value.setInterfaces(asList(interfaceValue));
		value.setInvocationHandler(handlerValue);

		assertThat(value.referencedValues()).containsExactly(interfaceValue, handlerValue);
	}

	@Test
	void testAddFields() throws Exception {
		SerializedProxy value = new SerializedProxy(Proxy.class);

		FieldSignature f1 = new FieldSignature(Object.class, Object.class, "f1");
		FieldSignature f2 = new FieldSignature(Object.class, Integer.class, "f2");
		value.addField(new SerializedField(f1, literal("str")));
		value.addField(new SerializedField(f2, literal(2)));

		assertThat(value.getFields()).containsExactly(
			new SerializedField(f1, literal("str")),
			new SerializedField(f2, literal(2)));
		assertThat(value.getField("f1")).map(field -> field.getValue()).contains(literal("str"));
		assertThat(value.getField("f2")).map(field -> field.getValue()).contains(literal(2));
		assertThat(value.getField("f3")).isNotPresent();
	}

	@Test
	void testToString() throws Exception {
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
