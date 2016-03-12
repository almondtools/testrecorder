package net.amygdalum.testrecorder.data;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.data.RandomDoubleValueGenerator;

public class RandomDoubleValueGeneratorTest {

	private RandomDoubleValueGenerator gen;

	@Before
	public void before() throws Exception {
		gen = new RandomDoubleValueGenerator();
	}

	@Test
	public void testCreateMax() throws Exception {
		gen.random.setSeed(Long.MAX_VALUE);

		assertThat(gen.create(null), equalTo(4.8025736165926125E23));
	}
	
	@Test
	public void testCreateMin() throws Exception {
		gen.random.setSeed(Long.MIN_VALUE);

		assertThat(gen.create(null), equalTo(-6.908855365943251E-24));
	}

}
