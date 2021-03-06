package net.amygdalum.testrecorder.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class RandomCharValueGeneratorTest {

	private RandomCharValueGenerator gen;

	@BeforeEach
	public void before() throws Exception {
		gen = new RandomCharValueGenerator();
	}

	@Nested
	class testCreate {
		@Test
		void testCreateMax() throws Exception {
			gen.random.setSeed(Long.MAX_VALUE);

			assertThat(gen.create(null)).isEqualTo((char) 27827);
		}

		@Test
		void testCreateMin() throws Exception {
			gen.random.setSeed(Long.MIN_VALUE);

			assertThat(gen.create(null)).isEqualTo((char) 46176);
		}
	}
}
