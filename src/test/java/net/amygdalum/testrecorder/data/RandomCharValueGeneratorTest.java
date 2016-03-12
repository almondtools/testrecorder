package net.amygdalum.testrecorder.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.data.RandomCharValueGenerator;

public class RandomCharValueGeneratorTest {

	private RandomCharValueGenerator gen;

	@Before
	public void before() throws Exception {
		gen = new RandomCharValueGenerator();
	}

	@Test
	public void testCreateMax() throws Exception {
		gen.random.setSeed(Long.MAX_VALUE);

		assertThat(gen.create(null), equalTo((char) 27827));
	}

	@Test
	public void testCreateMin() throws Exception {
		gen.random.setSeed(Long.MIN_VALUE);

		assertThat(gen.create(null), equalTo((char) 46176));
	}

}
