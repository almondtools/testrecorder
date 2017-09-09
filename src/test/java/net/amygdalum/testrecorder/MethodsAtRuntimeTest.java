package net.amygdalum.testrecorder;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

public class MethodsAtRuntimeTest {

	@Test
	public void testMatchDefaults() throws Exception {
		MethodsAtRuntime methodsAtRuntime = new MethodsAtRuntime() {
			
			@Override
			public boolean matches(Method field) {
				return true;
			}
		};
		assertThat(methodsAtRuntime.matches(anyMethod()), is(true));
		assertThat(methodsAtRuntime.matches(anyString(), anyString(), anyString()), is(false));
	}

	private String anyString() {
		return (String) null;
	}

	private Method anyMethod() {
		return (Method) null;
	}

}
