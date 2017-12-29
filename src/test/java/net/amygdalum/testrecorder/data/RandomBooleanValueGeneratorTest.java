package net.amygdalum.testrecorder.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RandomBooleanValueGeneratorTest {

	private RandomBooleanValueGenerator gen;

	@BeforeEach
	public void before() throws Exception {
		gen = new RandomBooleanValueGenerator();
	}

	@Test
	public void testCreateTrue() throws Exception {
		gen.random.setSeed(Integer.MAX_VALUE);

		assertThat(gen.create(null), equalTo(true));
	}

	@Test
	public void testCreateFalse() throws Exception {
		gen.random.setSeed(Integer.MIN_VALUE);

		assertThat(gen.create(null), equalTo(false));
	}

}
