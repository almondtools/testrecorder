package net.amygdalum.testrecorder.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RandomDoubleValueGeneratorTest {

	private RandomDoubleValueGenerator gen;

	@BeforeEach
	public void before() throws Exception {
		gen = new RandomDoubleValueGenerator();
	}

	@Test
	public void testCreateMax() throws Exception {
		gen.random.setSeed(Long.MAX_VALUE);

		assertThat(gen.create(null)).isEqualTo(4.8025736165926125E23);
	}
	
	@Test
	public void testCreateMin() throws Exception {
		gen.random.setSeed(Long.MIN_VALUE);

		assertThat(gen.create(null)).isEqualTo(-6.908855365943251E-24);
	}

}
