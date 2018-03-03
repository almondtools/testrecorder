package net.amygdalum.testrecorder.values;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.Deserializer;
import net.amygdalum.testrecorder.types.DeserializerContext;

public class SerializedImmutableTest {

	private SerializedImmutable<String> value;

	@BeforeEach
	public void before() throws Exception {
		value = new AnImmutable(String.class);
	}

	@Test
	public void testGetResultType() throws Exception {
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
	public void testSetGetValue() throws Exception {
		value.setValue("value");

		assertThat(value.getValue()).isEqualTo("value");
	}

	@Test
	public void testToString() throws Exception {
		assertThat(value.toString()).isNull();
	}

	private static class AnImmutable extends SerializedImmutable<String> {
		private AnImmutable(Type type) {
			super(type);
		}

		@Override
		public <T> T accept(Deserializer<T> visitor, DeserializerContext context) {
			return null;
		}

	}

}
