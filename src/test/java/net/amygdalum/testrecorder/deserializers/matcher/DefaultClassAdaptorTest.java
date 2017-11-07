package net.amygdalum.testrecorder.deserializers.matcher;

import static net.amygdalum.testrecorder.deserializers.DeserializerContext.NULL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultClassAdaptorTest {

	private DefaultClassAdaptor adaptor;

    @Before
    public void before() throws Exception {
        adaptor = new DefaultClassAdaptor();
    }

    @Test
    public void testParentNull() throws Exception {
        assertThat(adaptor.parent(), nullValue());
    }

    @Test
    public void testMatchesOnlyClass() throws Exception {
        assertThat(adaptor.matches(Class.class), is(true));
        assertThat(adaptor.matches(Object.class), is(false));
    }

    @Test
    public void testTryDeserialize() throws Exception {
        SerializedImmutable<Class<?>> value = new SerializedImmutable<>(Class.class);
        value.setValue(BigDecimal.class);
        MatcherGenerators generator = new MatcherGenerators(getClass());

        Computation result = adaptor.tryDeserialize(value, generator, NULL);

        assertThat(result.getStatements(), empty());
        assertThat(result.getValue(), equalTo("equalTo(java.math.BigDecimal.class)"));
    }

}
