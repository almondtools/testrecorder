package net.amygdalum.testrecorder.data;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class FixedIntValueGeneratorTest {

	private TestDataGenerator generator;

	@Before
	public void before() throws Exception {
		generator = new TestDataGenerator();
	}
	
	@Test
	public void testCreate() throws Exception {
		assertThat(new FixedIntValueGenerator(0x7fffffff).create(generator), is(0x7fffffff));
		assertThat(new FixedIntValueGenerator(0xffffffff).create(generator), is(0xffffffff));
	}

}
