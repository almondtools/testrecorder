package net.amygdalum.testrecorder.asm;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ByteCodeExceptionTest {

	@Test
	public void testByteCodeException() throws Exception {
		assertThat(new ByteCodeException(new IllegalArgumentException("msg")))
			.isInstanceOf(ByteCodeException.class)
			.satisfies(exception -> {
				assertThat(exception.getCause()).isInstanceOf(IllegalArgumentException.class);
				assertThat(exception.getCause()).hasMessage("msg");
			});
	}

}
