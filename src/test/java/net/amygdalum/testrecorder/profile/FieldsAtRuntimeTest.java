package net.amygdalum.testrecorder.profile;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

public class FieldsAtRuntimeTest {

	@Test
	public void testMatchDefaults() throws Exception {
		FieldsAtRuntime fieldsAtRuntime = new FieldsAtRuntime() {
			
			@Override
			public boolean matches(Field field) {
				return true;
			}
		};
		assertThat(fieldsAtRuntime.matches(anyField())).isTrue();
		assertThat(fieldsAtRuntime.matches(anyString(), anyString(), anyString())).isFalse();
	}

	private String anyString() {
		return (String) null;
	}

	private Field anyField() {
		return (Field) null;
	}

}
