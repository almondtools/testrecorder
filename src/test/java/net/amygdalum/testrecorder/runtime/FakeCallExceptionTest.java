package net.amygdalum.testrecorder.runtime;

import static com.almondtools.conmatch.exceptions.ExceptionMatcher.matchesException;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FakeCallExceptionTest {

	@Test
	public void testInputDecoratorException() throws Exception {
		assertThat(new FakeCallException(new IllegalArgumentException("msg")).getCause(), matchesException(IllegalArgumentException.class).withMessage("msg"));
	}

}
