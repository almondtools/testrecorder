package net.amygdalum.testrecorder.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FixedIntValueGeneratorTest {

	private TestDataGenerator generator;

	@BeforeEach
	public void before() throws Exception {
		generator = new TestDataGenerator();
	}
	
	@Test
	public void testCreate() throws Exception {
		assertThat(new FixedIntValueGenerator(0x7fffffff).create(generator)).isEqualTo(0x7fffffff);
		assertThat(new FixedIntValueGenerator(0xffffffff).create(generator)).isEqualTo(0xffffffff);
	}

}
