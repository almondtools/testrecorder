package net.amygdalum.testrecorder.asm;

import static com.almondtools.conmatch.exceptions.ExceptionMatcher.matchesException;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ByteCodeExceptionTest {

	@Test
	public void testByteCodeException() throws Exception {
		assertThat(new ByteCodeException(new IllegalArgumentException("msg")),matchesException(ByteCodeException.class)
			.withCause(matchesException(IllegalArgumentException.class).withMessage("msg")));
	}

}
