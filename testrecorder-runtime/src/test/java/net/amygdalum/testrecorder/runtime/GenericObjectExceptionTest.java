package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class GenericObjectExceptionTest {

	@Test
	public void testGenericObjectException() throws Exception {
		assertThat(new GenericObjectException().getMessage()).isNull();
		assertThat(new GenericObjectException("msg", new RuntimeException()).getMessage()).isEqualTo("msg");
		assertThat(new GenericObjectException("msg", new RuntimeException()))
			.hasMessage("msg")
			.satisfies(exception -> assertThat(exception.getCause())
				.isInstanceOf(RuntimeException.class));
		assertThat(new GenericObjectException("msg", new Throwable[] { new IllegalArgumentException(), new IllegalStateException() }))
			.hasMessage("msg")
			.satisfies(exception -> assertThat(exception.getSuppressed())
				.hasSize(2)
				.hasOnlyElementsOfTypes(IllegalArgumentException.class, IllegalStateException.class));
	}
}
