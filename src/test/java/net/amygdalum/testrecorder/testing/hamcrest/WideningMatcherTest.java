package net.amygdalum.testrecorder.testing.hamcrest;

import static net.amygdalum.testrecorder.testing.hamcrest.WideningMatcher.widening;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
        assertThat(widening(equalTo("x")).matches("x")).isTrue();
        assertThat(widening(equalTo("x")).matches("y")).isFalse();
    }

}
