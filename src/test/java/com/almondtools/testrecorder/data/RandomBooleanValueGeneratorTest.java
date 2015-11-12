package com.almondtools.testrecorder.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class RandomBooleanValueGeneratorTest {

	private RandomBooleanValueGenerator gen;

	@Before
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
