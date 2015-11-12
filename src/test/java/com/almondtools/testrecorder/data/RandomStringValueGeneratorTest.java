package com.almondtools.testrecorder.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class RandomStringValueGeneratorTest {

	private RandomStringValueGenerator gen;

	@Before
	public void before() throws Exception {
		gen = new RandomStringValueGenerator("A","b");
	}

	@Test
	public void testCreateMax() throws Exception {
		gen.random.setSeed(Long.MAX_VALUE);

		assertThat(gen.create(null), equalTo("A"));
	}

	@Test
	public void testCreateMin() throws Exception {
		gen.random.setSeed(Long.MIN_VALUE);

		assertThat(gen.create(null), equalTo("b"));
	}

}
