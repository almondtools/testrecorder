package net.amygdalum.testrecorder.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.data.RandomShortValueGenerator;

public class RandomShortValueGeneratorTest {

	private RandomShortValueGenerator gen;

	@Before
	public void before() throws Exception {
		gen = new RandomShortValueGenerator();
	}

	@Test
	public void testCreateMax() throws Exception {
		gen.random.setSeed(Long.MAX_VALUE);

		assertThat(gen.create(null), equalTo((short) 27827));
	}

	@Test
	public void testCreateMin() throws Exception {
		gen.random.setSeed(Long.MIN_VALUE);

		assertThat(gen.create(null), equalTo((short) -19360));
	}

}
