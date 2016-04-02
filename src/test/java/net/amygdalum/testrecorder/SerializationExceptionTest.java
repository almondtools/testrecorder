package net.amygdalum.testrecorder;

import static com.almondtools.conmatch.exceptions.ExceptionMatcher.matchesException;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class SerializationExceptionTest {

	@Test
	public void testSerializationException() throws Exception {
		assertThat(new SerializationException(new IllegalArgumentException("msg")).getCause(), matchesException(IllegalArgumentException.class).withMessage("msg"));
	}

}
