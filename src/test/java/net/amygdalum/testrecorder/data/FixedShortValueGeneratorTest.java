package net.amygdalum.testrecorder.data;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class FixedShortValueGeneratorTest {

	private TestDataGenerator generator;

	@Before
	public void before() throws Exception {
		generator = new TestDataGenerator();
	}
	
	@Test
	public void testCreate() throws Exception {
		assertThat(new FixedShortValueGenerator((short) 0x7fff).create(generator), is((short) 0x7fff));
		assertThat(new FixedShortValueGenerator((short) 0xffff).create(generator), is((short) 0xffff));
	}

}
