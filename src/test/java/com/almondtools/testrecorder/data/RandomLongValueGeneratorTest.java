package com.almondtools.testrecorder.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class RandomLongValueGeneratorTest {

	private RandomLongValueGenerator gen;

	@Before
	public void before() throws Exception {
		gen = new RandomLongValueGenerator();
	}

	@Test
	public void testCreateMax() throws Exception {
		gen.random.setSeed(Long.MAX_VALUE);

		assertThat(gen.create(null), equalTo(4961115982468162243l));
	}

	@Test
	public void testCreateMin() throws Exception {
		gen.random.setSeed(Long.MIN_VALUE);

		assertThat(gen.create(null), equalTo(-4962768465676381896l));
	}

}
