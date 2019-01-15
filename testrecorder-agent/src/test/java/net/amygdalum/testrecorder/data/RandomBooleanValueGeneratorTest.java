package net.amygdalum.testrecorder.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class RandomBooleanValueGeneratorTest {

	private RandomBooleanValueGenerator gen;

	@BeforeEach
	public void before() throws Exception {
		gen = new RandomBooleanValueGenerator();
	}

	@Nested
	class testCreate {
		@Test
		void onTrue() throws Exception {
			gen.random.setSeed(Integer.MAX_VALUE);

			assertThat(gen.create(null)).isEqualTo(true);
		}

		@Test
		void onFalse() throws Exception {
			gen.random.setSeed(Integer.MIN_VALUE);

			assertThat(gen.create(null)).isEqualTo(false);
		}
	}
}
