package net.amygdalum.testrecorder.values;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class AbstractSerializedValueTest {

	@Test
	public void testASerializedValue() throws Exception {
		ASerializedValue value = new ASerializedValue(String.class);

		assertThat(value.getType()).isSameAs(String.class);
		assertThat(value.getUsedTypes()).containsExactly(String.class);
	}

}
