package net.amygdalum.testrecorder.runtime;

import static net.amygdalum.testrecorder.runtime.EnumMatcher.matchingEnum;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.StringDescription;
import org.junit.Test;

public class EnumMatcherTest {

    @Test
    public void testDescribeTo() throws Exception {
        StringDescription description = new StringDescription();

        matchingEnum("VALUE").describeTo(description);

        assertThat(description.toString(), equalTo("with name \"VALUE\""));
    }

    @Test
    public void testMatchesSafelyWithSuccess() throws Exception {
        boolean matches = matchingEnum("VALUE").matchesSafely((Enum<?>) MyEnum.VALUE);

        assertThat(matches, is(true));
    }

    @Test
    public void testMatchesSafelyWithFailure() throws Exception {
        boolean matches = matchingEnum("NOVALUE").matchesSafely((Enum<?>) MyEnum.VALUE);

        assertThat(matches, is(false));
    }

}

enum MyEnum {
    VALUE;
}
