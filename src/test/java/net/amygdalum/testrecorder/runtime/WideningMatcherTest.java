package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.testrecorder.runtime.WideningMatcher.widening;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.StringDescription;
import org.junit.jupiter.api.Test;

public class WideningMatcherTest {

    @Test
    public void testDescribeTo() throws Exception {
        StringDescription description = new StringDescription();
        
        widening(equalTo("x")).describeTo(description);
        
        assertThat(description.toString()).isEqualTo("\"x\"");
    }

    @Test
    public void testMatches() throws Exception {
        assertThat(widening(equalTo("x")).matches("x"), is(true));
        assertThat(widening(equalTo("x")).matches("y"), is(false));
    }

}
