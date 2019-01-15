package net.amygdalum.testrecorder.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class RandomFloatValueGeneratorTest {

	private RandomFloatValueGenerator gen;

	@BeforeEach
	public void before() throws Exception {
		gen = new RandomFloatValueGenerator();
	}

	@Nested
	class testCreate {
		@Test
		void onMax() throws Exception {
			gen.random.setSeed(Long.MAX_VALUE);

			assertThat(gen.create(null)).isEqualTo(1739.3969F);
		}

		@Test
		void onMin() throws Exception {
			gen.random.setSeed(Long.MIN_VALUE);

			assertThat(gen.create(null)).isEqualTo(-0.0024521574F);
		}
	}
}
