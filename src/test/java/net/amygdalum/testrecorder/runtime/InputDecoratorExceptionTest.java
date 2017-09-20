package net.amygdalum.testrecorder.runtime;

import static com.almondtools.conmatch.exceptions.ExceptionMatcher.matchesException;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class InputDecoratorExceptionTest {

	@Test
	public void testInputDecoratorException() throws Exception {
		assertThat(new InputDecoratorException(new IllegalArgumentException("msg")).getCause(), matchesException(IllegalArgumentException.class).withMessage("msg"));
	}

}
