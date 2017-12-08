package net.amygdalum.testrecorder.types;

import static com.almondtools.conmatch.exceptions.ExceptionMatcher.matchesException;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DeserializationExceptionTest {

	@Test
	public void testDeserializationException() throws Exception {
		assertThat(new DeserializationException("main msg"), matchesException(DeserializationException.class)
			.withMessage("main msg"));
		assertThat(new DeserializationException("main msg", new IllegalArgumentException("msg")), matchesException(DeserializationException.class)
			.withCause(IllegalArgumentException.class)
			.withMessage("main msg"));
		assertThat(new DeserializationException(new IllegalArgumentException("msg")),matchesException(DeserializationException.class)
			.withCause(matchesException(IllegalArgumentException.class).withMessage("msg")));
	}

}
