package net.amygdalum.testrecorder.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FixedDoubleValueGeneratorTest {

	private TestDataGenerator generator;

	@BeforeEach
	public void before() throws Exception {
		generator = new TestDataGenerator();
	}
	
	@Test
	public void testCreate() throws Exception {
		assertThat(new FixedDoubleValueGenerator(0.01348e-7d).create(generator)).isEqualTo(0.01348e-7d);
		assertThat(new FixedDoubleValueGenerator(1267.434e2d).create(generator)).isEqualTo(1267.434e2d);
	}

}
