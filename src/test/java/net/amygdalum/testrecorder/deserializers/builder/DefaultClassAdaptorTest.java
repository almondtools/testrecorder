package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedImmutable;

public class DefaultClassAdaptorTest {

	private DefaultClassAdaptor adaptor;

    @BeforeEach
    public void before() throws Exception {
        adaptor = new DefaultClassAdaptor();
    }

    @Test
    public void testParentNull() throws Exception {
        assertThat(adaptor.parent(), nullValue());
    }

    @Test
    public void testMatchesOnlyBigInteger() throws Exception {
        assertThat(adaptor.matches(Class.class), is(true));
        assertThat(adaptor.matches(Object.class), is(false));
    }

    @Test
    public void testTryDeserialize() throws Exception {
        SerializedImmutable<Class<?>> value = new SerializedImmutable<>(Class.class);
        value.setValue(BigInteger.class);
        SetupGenerators generator = new SetupGenerators(getClass());

        Computation result = adaptor.tryDeserialize(value, generator, NULL);

        assertThat(result.getStatements(), empty());
        assertThat(result.getValue(), equalTo("java.math.BigInteger.class"));
    }

}
