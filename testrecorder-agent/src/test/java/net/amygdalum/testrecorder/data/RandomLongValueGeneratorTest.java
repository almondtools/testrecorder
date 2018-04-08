package net.amygdalum.testrecorder.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RandomLongValueGeneratorTest {

	private RandomLongValueGenerator gen;

	@BeforeEach
	public void before() throws Exception {
		gen = new RandomLongValueGenerator();
	}

	@Test
	public void testCreateMax() throws Exception {
		gen.random.setSeed(Long.MAX_VALUE);

		assertThat(gen.create(null)).isEqualTo(4961115982468162243l);
	}

	@Test
	public void testCreateMin() throws Exception {
		gen.random.setSeed(Long.MIN_VALUE);

		assertThat(gen.create(null)).isEqualTo(-4962768465676381896l);
	}

}
