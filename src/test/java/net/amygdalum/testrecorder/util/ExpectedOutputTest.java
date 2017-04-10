package net.amygdalum.testrecorder.util;

import static com.almondtools.conmatch.exceptions.ExceptionMatcher.matchesException;
import static com.almondtools.conmatch.strings.WildcardStringMatcher.containsPattern;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.PrintWriter;

import org.junit.Test;

public class ExpectedOutputTest {

    @Test
    public void testExpectSuccesful() throws Exception {
        ExpectedOutput out = new ExpectedOutput();
        out.expect(Writer.class, "write", equalTo("asdf"));
        
        out.notifyOutput(Writer.class, "write", "asdf");
        
        assertThat(Throwables.capture(out::verify), nullValue());
    }
    
    @Test
    public void testNotifyFailingClass() throws Exception {
        ExpectedOutput out = new ExpectedOutput();
        out.expect(Writer.class, "write", equalTo("asdf"));
        
        assertThat(Throwables.capture(() -> out.notifyOutput(PrintWriter.class, "write", "asdf")), matchesException(AssertionError.class)
            .withMessage(containsPattern("expected output Writer.write(<\"asdf\">)*but found PrintWriter.write(\"asdf\")")));
    }
    
    @Test
    public void testNotifyFailingMethod() throws Exception {
        ExpectedOutput out = new ExpectedOutput();
        out.expect(Writer.class, "write", equalTo("asdf"));
        
        assertThat(Throwables.capture(() -> out.notifyOutput(Writer.class, "writeOther", "asdf")), matchesException(AssertionError.class)
            .withMessage(containsPattern("expected output Writer.write(<\"asdf\">)*but found Writer.writeOther(\"asdf\")")));
    }
    
    @Test
    public void testNotifyFailingValue() throws Exception {
        ExpectedOutput out = new ExpectedOutput();
        out.expect(Writer.class, "write", equalTo("asdf"));
        
        assertThat(Throwables.capture(() -> out.notifyOutput(Writer.class, "write", "fdsa")), matchesException(AssertionError.class)
            .withMessage(containsPattern("expected output Writer.write(<\"asdf\">)*but found Writer.write(\"fdsa\")")));
    }
    
    @Test
    public void testExpectFailing() throws Exception {
        ExpectedOutput out = new ExpectedOutput();
        out.expect(Writer.class, "write", equalTo("asdf"));
        
        assertThat(Throwables.capture(out::verify), matchesException(AssertionError.class)
            .withMessage(containsPattern("expected (but not found) output*asdf")));
    }
    
    static class Writer {
        void write (String s) {
            
        }
    }

}
