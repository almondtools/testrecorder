package net.amygdalum.testrecorder.types;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SerializationExceptionTest {

	@Test
	public void testSerializationException() throws Exception {
		assertThat(new SerializationException(new IllegalArgumentException("msg")))
			.isInstanceOf(SerializationException.class)
			.satisfies(exception -> assertThat(exception.getCause())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("msg"));
	}

}
