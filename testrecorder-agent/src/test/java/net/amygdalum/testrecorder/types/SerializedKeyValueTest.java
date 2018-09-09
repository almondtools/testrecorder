package net.amygdalum.testrecorder.types;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SerializedKeyValueTest {

	@Test
	void testGetKeyValue() throws Exception {
		assertThat(new SerializedKeyValue(literal("key"), literal(2)).getKey()).isEqualTo(literal("key"));
		assertThat(new SerializedKeyValue(literal("key"), literal(2)).getValue()).isEqualTo(literal(2));
	}

	@Test
	public void testGetAnnotations() throws Exception {
		assertThat(new SerializedKeyValue(literal("key"), literal(2)).getAnnotations()).isEmpty();
	}

	@Test
	public void testAccept() throws Exception {
		assertThat(new SerializedKeyValue(literal("key"), literal(2))
			.accept(new TestValueVisitor())).isEqualTo("keyvalue");
	}

	@Test
	public void testToString() throws Exception {
		assertThat(new SerializedKeyValue(literal("key"), literal(2)).toString()).isEqualTo("key:2");
	}

	@Test
	void testEquals() throws Exception {
		assertThat(new SerializedKeyValue(literal("key"), literal(2))).satisfies(defaultEquality()
			.andEqualTo(new SerializedKeyValue(literal("key"), literal(2)))
			.andNotEqualTo(new SerializedKeyValue(literal("key2"), literal(2)))
			.andNotEqualTo(new SerializedKeyValue(literal("key"), literal(3)))
			.conventions());
	}

}
