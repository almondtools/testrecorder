package net.amygdalum.testrecorder.util;

import static com.almondtools.conmatch.exceptions.ExceptionMatcher.matchesException;
import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.Writer;

import org.junit.Test;

import net.amygdalum.testrecorder.runtime.PrimitiveArrayMatcher;
import net.amygdalum.testrecorder.runtime.Throwables;
import net.amygdalum.testrecorder.util.SetupInput;

public class SetupInputTest {

    @Test
    public void testProvide() throws Exception {
        SetupInput setupInput = new SetupInput();
        setupInput.provide(Reader.class, "read", 4, 42);

        Object input = setupInput.requestInput(Reader.class, "read", 42);

        assertThat(input, equalTo(4));
    }

    @Test
    public void testProvideNotExpectedClass() throws Exception {
        SetupInput setupInput = new SetupInput();
        setupInput.provide(Reader.class, "read", 4, 42);

        Throwable captured = Throwables.capture(() -> setupInput.requestInput(Writer.class, "read", 42));

        assertThat(captured, matchesException(AssertionError.class)
            .withMessage(containsPattern("expected input net.amygdalum.testrecorder.util.SetupInputTest$Reader, but found java.io.Writer")));
    }
    
    @Test
    public void testProvideNotExpectedMethod() throws Exception {
        SetupInput setupInput = new SetupInput();
        setupInput.provide(Reader.class, "read", 4, 42);

        Throwable captured = Throwables.capture(() -> setupInput.requestInput(Reader.class, "readOther", 42));

        assertThat(captured, matchesException(AssertionError.class)
            .withMessage(containsPattern("expected input read, but found readOther")));
    }

    @Test
    public void testProvideNotExpectedValue() throws Exception {
        SetupInput setupInput = new SetupInput();
        setupInput.provide(Reader.class, "read", 4, 42);

        Throwable captured = Throwables.capture(() -> setupInput.requestInput(Reader.class, "read"));

        assertThat(captured, matchesException(AssertionError.class)
            .withMessage(containsPattern("expected input 1 arguments, but found 0 arguments")));
    }

    @Test
    public void testProvideFailedSync() throws Exception {
        SetupInput setupInput = new SetupInput();
        setupInput.provide(Reader.class, "read", 4, 42);

        Throwable captured = Throwables.capture(() -> setupInput.requestInput(Reader.class, "read", "string"));

        assertThat(captured, matchesException(AssertionError.class)
            .withMessage(containsPattern("expected argument type java.lang.Integer, but found java.lang.String")));
    }
    
    @Test
    public void testProvideSyncArray() throws Exception {
        SetupInput setupInput = new SetupInput();
        setupInput.provide(Reader.class, "read", 4, (Object) "asdf".toCharArray());

        char[] input = new char[4];
        setupInput.requestInput(Reader.class, "read", (Object) input);

        assertThat(input, PrimitiveArrayMatcher.charArrayContaining('a','s','d','f'));
    }
    
    static class Reader {
        int read(int i) {
            return 0;
        }
    }

}
