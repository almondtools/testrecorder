package net.amygdalum.testrecorder.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FixedByteValueGeneratorTest {

	private TestDataGenerator generator;

	@BeforeEach
	public void before() throws Exception {
		generator = new TestDataGenerator();
	}
	
	@Test
	public void testCreate() throws Exception {
		assertThat(new FixedByteValueGenerator((byte) -127).create(generator)).isEqualTo((byte)-127);
		assertThat(new FixedByteValueGenerator((byte) 128).create(generator)).isEqualTo((byte) 128);
	}

}
