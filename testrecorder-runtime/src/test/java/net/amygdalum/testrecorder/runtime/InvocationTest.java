package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Simple;

public class InvocationTest {

	@Test
	void testCapture() throws Exception {
		Simple object = new Simple();
		Invocation invocation = Invocation.capture(object, Simple.class, "getStr", "()Ljava/lang/String;");
		
		assertThat(invocation.instance).isSameAs(object);
		assertThat(invocation.clazz).isSameAs(Simple.class);
		assertThat(invocation.methodName).isEqualTo("getStr");
		assertThat(invocation.methodDesc).isEqualTo("()Ljava/lang/String;");
	}

}
