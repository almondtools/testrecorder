package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.PrimitiveSimple;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class PlaceHolderInvocationHandlerTest {

	@Test
	void testInvoke() throws Throwable {
		Object result = new PlaceHolderInvocationHandler().invoke(new Simple(), Simple.class.getDeclaredMethod("getStr"), new Object[0]);
		
		assertThat(result).isEqualTo(null);
	}

	@Test
	void testInvokeWithArgument() throws Throwable {
		Object result = new PlaceHolderInvocationHandler().invoke(new PrimitiveSimple(), PrimitiveSimple.class.getDeclaredMethod("setI", int.class), new Object[] {42});
		
		assertThat(result).isEqualTo(null);
	}

	@Test
	void testInvokePrimitive() throws Throwable {
		Object result = new PlaceHolderInvocationHandler().invoke(new PrimitiveSimple(), PrimitiveSimple.class.getDeclaredMethod("getI"), new Object[0]);
		
		assertThat(result).isEqualTo(Integer.valueOf(0));
	}

}
