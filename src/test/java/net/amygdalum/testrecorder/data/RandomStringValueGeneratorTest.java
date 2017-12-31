package net.amygdalum.testrecorder.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RandomStringValueGeneratorTest {

	private RandomStringValueGenerator gen;

	@BeforeEach
	public void before() throws Exception {
		gen = new RandomStringValueGenerator("A","b");
	}

	@Test
	public void testCreateMax() throws Exception {
		gen.random.setSeed(Long.MAX_VALUE);

		assertThat(gen.create(null)).isEqualTo("A");
	}

	@Test
	public void testCreateMin() throws Exception {
		gen.random.setSeed(Long.MIN_VALUE);

		assertThat(gen.create(null)).isEqualTo("b");
	}

}
