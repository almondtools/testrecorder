package net.amygdalum.testrecorder.runtime;

import static com.almondtools.conmatch.exceptions.ExceptionMatcher.matchesException;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class OutputDecoratorExceptionTest {

	@Test
	public void testInputDecoratorException() throws Exception {
		assertThat(new OutputDecoratorException(new IllegalArgumentException("msg")).getCause(), matchesException(IllegalArgumentException.class).withMessage("msg"));
	}

}
