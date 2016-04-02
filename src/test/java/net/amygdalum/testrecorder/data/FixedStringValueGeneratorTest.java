package net.amygdalum.testrecorder.data;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class FixedStringValueGeneratorTest {

	private TestDataGenerator generator;

	@Before
	public void before() throws Exception {
		generator = new TestDataGenerator();
	}
	
	@Test
	public void testCreate() throws Exception {
		assertThat(new FixedStringValueGenerator("Foo").create(generator), is("Foo"));
		assertThat(new FixedStringValueGenerator("Bar").create(generator), is("Bar"));
	}

}
