package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.types.DeserializerContext.NULL;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.TestValueVisitor;

public class SerializedLiteralTest {

	@Test
	public void testLiteral() throws Exception {
		SerializedLiteral value = literal("string");
		SerializedLiteral testvalue = literal("string");

		assertThat(testvalue).isSameAs(value);
	}

	@Test
		public void testGetUsedTypes() throws Exception {
			SerializedLiteral value = literal("string");
	
			assertThat(value.getUsedTypes()).containsExactly(String.class);
		}

	@Test
	public void testGetValue() throws Exception {
		SerializedLiteral value = literal("string");

		assertThat(value.getValue()).isEqualTo("string");
	}

	@Test
	public void testAccept() throws Exception {
		SerializedLiteral value = literal("string");

		assertThat(value.accept(new TestValueVisitor(), NULL)).isEqualTo("SerializedLiteral");
	}

	@Test
	public void testToString() throws Exception {
		SerializedLiteral value = literal("string");

		assertThat(value.toString()).isEqualTo("string");
	}

}
