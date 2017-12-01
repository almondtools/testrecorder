package net.amygdalum.testrecorder.profile;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import org.junit.Test;

public class FieldsAtRuntimeTest {

	@Test
	public void testMatchDefaults() throws Exception {
		FieldsAtRuntime fieldsAtRuntime = new FieldsAtRuntime() {
			
			@Override
			public boolean matches(Field field) {
				return true;
			}
		};
		assertThat(fieldsAtRuntime.matches(anyField()), is(true));
		assertThat(fieldsAtRuntime.matches(anyString(), anyString(), anyString()), is(false));
	}

	private String anyString() {
		return (String) null;
	}

	private Field anyField() {
		return (Field) null;
	}

}
