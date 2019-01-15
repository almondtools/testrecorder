package net.amygdalum.testrecorder.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class RandomByteValueGeneratorTest {

	private RandomByteValueGenerator gen;

	@BeforeEach
	public void before() throws Exception {
		gen = new RandomByteValueGenerator();
	}

	@Nested
	class testCreate {
		@Test
		void onMax() throws Exception {
			gen.random.setSeed(Long.MAX_VALUE);

			assertThat(gen.create(null)).isEqualTo((byte) -77);
		}

		@Test
		void onMin() throws Exception {
			gen.random.setSeed(Long.MIN_VALUE);

			assertThat(gen.create(null)).isEqualTo((byte) 96);
		}
	}
}
