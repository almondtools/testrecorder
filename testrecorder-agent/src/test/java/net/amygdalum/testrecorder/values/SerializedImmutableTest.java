package net.amygdalum.testrecorder.values;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.RoleVisitor;

public class SerializedImmutableTest {

	private SerializedImmutable<String> value;

	@BeforeEach
	public void before() throws Exception {
		value = new AnImmutable(String.class);
	}

	@Test
	public void testGetUsedTypes() throws Exception {
		assertThat(value.getUsedTypes()).containsExactly(String.class);
	}

	@Test
	public void testReferencedValues() throws Exception {
		assertThat(value.referencedValues()).isEmpty();
	}

	@Test
	public void testWithValue() throws Exception {
		value = value.withValue("newvalue");

		assertThat(value.getValue()).isEqualTo("newvalue");
	}

	@Test
	public void testSetValue() throws Exception {
		value.setValue("value");

		assertThat(value.getValue()).isEqualTo("value");
	}

	@Test
	public void testToString() throws Exception {
		assertThat(value.toString()).isNull();
	}

	private static class AnImmutable extends SerializedImmutable<String> {
		private AnImmutable(Class<?> type) {
			super(type);
		}

		@Override
		public <T> T accept(RoleVisitor<T> visitor) {
			return null;
		}

	}

}
