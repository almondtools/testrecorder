package net.amygdalum.testrecorder.deserializers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DeserializerContextTest {

    @Test
    public void testGetHints() throws Exception {
        assertThat(DeserializerContext.newContext("1").getHints(Integer.class).toArray(len -> new Integer[len]), emptyArray());
        assertThat(DeserializerContext.newContext(1).getHints(Integer.class).toArray(len -> new Integer[len]), arrayContaining(1));
    }

    @Test
    public void testGetHint() throws Exception {
        assertThat(DeserializerContext.newContext("1").getHint(Integer.class).isPresent(), is(false));
        assertThat(DeserializerContext.newContext(1).getHint(Integer.class).get(), equalTo(1));
    }

}
