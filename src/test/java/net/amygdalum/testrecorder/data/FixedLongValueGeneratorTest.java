package net.amygdalum.testrecorder.data;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FixedLongValueGeneratorTest {

	private TestDataGenerator generator;

	@BeforeEach
	public void before() throws Exception {
		generator = new TestDataGenerator();
	}
	
	@Test
	public void testCreate() throws Exception {
		assertThat(new FixedLongValueGenerator(0x7fffffffffffffffl).create(generator), is(0x7fffffffffffffffl));
		assertThat(new FixedLongValueGenerator(0xffffffffffffffffl).create(generator), is(0xffffffffffffffffl));
	}

}
