package net.amygdalum.testrecorder.data;

import static org.assertj.core.api.Assertions.assertThat;

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
		assertThat(new FixedLongValueGenerator(0x7fffffffffffffffl).create(generator)).isEqualTo(0x7fffffffffffffffl);
		assertThat(new FixedLongValueGenerator(0xffffffffffffffffl).create(generator)).isEqualTo(0xffffffffffffffffl);
	}

}
