package net.amygdalum.testrecorder.runtime;

import static com.almondtools.conmatch.exceptions.ExceptionMatcher.matchesException;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;

public class GenericObjectExceptionTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testGenericObjectException() throws Exception {
        assertThat(new GenericObjectException().getMessage(), nullValue());
        assertThat(new GenericObjectException("msg", new RuntimeException()).getMessage(), equalTo("msg"));
        assertThat(new GenericObjectException("msg", new RuntimeException()).getCause(), matchesException(RuntimeException.class));
        assertThat(new GenericObjectException("msg", new Throwable[] { new IllegalArgumentException(), new IllegalStateException() }).getMessage(), equalTo("msg"));
        assertThat(new GenericObjectException("msg", new Throwable[] { new IllegalArgumentException(), new IllegalStateException() }).getSuppressed(),
            arrayContaining(matchesException(IllegalArgumentException.class), matchesException(IllegalStateException.class)));
    }
}
