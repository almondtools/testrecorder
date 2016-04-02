package net.amygdalum.testrecorder.data;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class FixedByteValueGeneratorTest {

	private TestDataGenerator generator;

	@Before
	public void before() throws Exception {
		generator = new TestDataGenerator();
	}
	
	@Test
	public void testCreate() throws Exception {
		assertThat(new FixedByteValueGenerator((byte) -127).create(generator), is((byte)-127));
		assertThat(new FixedByteValueGenerator((byte) 128).create(generator), is((byte) 128));
	}

}
