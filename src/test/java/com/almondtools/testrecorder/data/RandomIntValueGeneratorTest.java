package com.almondtools.testrecorder.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class RandomIntValueGeneratorTest {

	private RandomIntValueGenerator gen;

	@Before
	public void before() throws Exception {
		gen = new RandomIntValueGenerator();
	}

	@Test
	public void testCreateMax() throws Exception {
		gen.random.setSeed(Long.MAX_VALUE);

		assertThat(gen.create(null), equalTo(1155099827));
	}

	@Test
	public void testCreateMin() throws Exception {
		gen.random.setSeed(Long.MIN_VALUE);

		assertThat(gen.create(null), equalTo(-1155484576));
	}

}
