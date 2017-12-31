package net.amygdalum.testrecorder;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

public class MethodsAtRuntimeTest {

	@Test
	public void testMatchDefaults() throws Exception {
		MethodsAtRuntime methodsAtRuntime = new MethodsAtRuntime() {
			
			@Override
			public boolean matches(Method field) {
				return true;
			}
		};
		assertThat(methodsAtRuntime.matches(anyMethod())).isTrue();
		assertThat(methodsAtRuntime.matches(anyString(), anyString(), anyString())).isFalse();
	}

	private String anyString() {
		return (String) null;
	}

	private Method anyMethod() {
		return (Method) null;
	}

}
