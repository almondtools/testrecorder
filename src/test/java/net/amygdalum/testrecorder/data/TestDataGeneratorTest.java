package net.amygdalum.testrecorder.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestDataGeneratorTest {

	private TestDataGenerator gen;

	@BeforeEach
	public void before() throws Exception {
		gen = new TestDataGenerator();
	}

	@Test
	public void testCreateTrue() throws Exception {
		gen.withValues(boolean.class, new FixedBooleanValueGenerator(true));

		assertThat(gen.create(boolean.class)).isTrue();
	}

	@Test
	public void testCreateFalse() throws Exception {
		gen.withValues(boolean.class, new FixedBooleanValueGenerator(false));

		assertThat(gen.create(boolean.class)).isFalse();
	}

	@Test
	public void testCreateChar() throws Exception {
		gen.withValues(char.class, new FixedCharValueGenerator('j'));

		assertThat(gen.create(char.class)).isEqualTo('j');
	}

	@Test
	public void testCreateByte() throws Exception {
		gen.withValues(byte.class, new FixedByteValueGenerator((byte) 123));

		assertThat(gen.create(byte.class)).isEqualTo((byte) 123);
	}

	@Test
	public void testCreateShort() throws Exception {
		gen.withValues(short.class, new FixedShortValueGenerator((short) 244));

		assertThat(gen.create(short.class)).isEqualTo((short) 244);
	}

	@Test
	public void testCreateInt() throws Exception {
		gen.withValues(int.class, new FixedIntValueGenerator(67777));

		assertThat(gen.create(int.class)).isEqualTo(67777);
	}

	@Test
	public void testCreateLong() throws Exception {
		gen.withValues(long.class, new FixedLongValueGenerator(900000000l));

		assertThat(gen.create(long.class)).isEqualTo(900000000l);
	}

	@Test
	public void testCreateFloat() throws Exception {
		gen.withValues(float.class, new FixedFloatValueGenerator(1.23e-4f));

		assertThat(gen.create(float.class)).isEqualTo(1.23e-4f);
	}

	@Test
	public void testCreateDouble() throws Exception {
		gen.withValues(double.class, new FixedDoubleValueGenerator(0.2134234e-22d));

		assertThat(gen.create(double.class)).isEqualTo(0.2134234e-22d);
	}

	@Test
	public void testCreateString() throws Exception {
		gen.withValues(String.class, new FixedStringValueGenerator("string"));

		assertThat(gen.create(String.class)).isEqualTo("string");
	}

	@Test
	public void testCreateDefault() throws Exception {
		assertThat(gen.create(boolean.class)).isFalse();
		assertThat(gen.create(char.class)).isEqualTo((char) 0);
		assertThat(gen.create(byte.class)).isEqualTo((byte) 0);
		assertThat(gen.create(short.class)).isEqualTo((short) 0);
		assertThat(gen.create(int.class)).isEqualTo(0);
		assertThat(gen.create(long.class)).isEqualTo(0l);
		assertThat(gen.create(float.class)).isEqualTo(0f);
		assertThat(gen.create(double.class)).isEqualTo(0d);
		assertThat(gen.create(Object.class)).isNull();
	}

	@Test
	public void testCreateGeneric() throws Exception {
		gen
			.withValues(MyObject.class, new GenericObjectGenerator<>(MyObject.class))
			.withValues(int.class, new FixedIntValueGenerator(42))
			.withValues(String.class, new RandomStringValueGenerator(" is the solution"));

		assertThat(gen.create(MyObject.class).toString()).isEqualTo("42 is the solution");
	}

	private static class MyObject {
		private int i;
		private String s;

		@Override
		public String toString() {
			return i + s;
		}
	}

}
