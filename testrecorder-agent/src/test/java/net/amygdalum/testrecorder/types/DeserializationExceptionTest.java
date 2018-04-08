package net.amygdalum.testrecorder.types;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class DeserializationExceptionTest {

	@Test
	public void testDeserializationException() throws Exception {
		assertThat(new DeserializationException("main msg"))
			.isInstanceOf(DeserializationException.class)
			.hasMessage("main msg");
		assertThat(new DeserializationException("main msg", new IllegalArgumentException("msg")))
			.isInstanceOf(DeserializationException.class)
			.hasMessage("main msg")
			.satisfies(exception -> assertThat(exception.getCause())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("msg"));
		assertThat(new DeserializationException(new IllegalArgumentException("msg")))
			.isInstanceOf(DeserializationException.class)
			.satisfies(exception -> assertThat(exception.getCause())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("msg"));
	}

}
