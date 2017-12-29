package net.amygdalum.testrecorder.runtime;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.EmptyEnum;
import net.amygdalum.testrecorder.util.testobjects.OrthogonalInterface;
import net.amygdalum.testrecorder.util.testobjects.PublicEnum;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class NonDefaultValueTest {

    @Test
    public void testGetNonDefaultValue() throws Exception {
        assertThat(NonDefaultValue.of(boolean.class), equalTo(true));
        assertThat(NonDefaultValue.of(char.class), equalTo((char) 1));
        assertThat(NonDefaultValue.of(byte.class), equalTo((byte) 1));
        assertThat(NonDefaultValue.of(short.class), equalTo((short) 1));
        assertThat(NonDefaultValue.of(int.class), equalTo((int) 1));
        assertThat(NonDefaultValue.of(float.class), equalTo((float) 1));
        assertThat(NonDefaultValue.of(long.class), equalTo((long) 1));
        assertThat(NonDefaultValue.of(double.class), equalTo((double) 1));
        assertThat(NonDefaultValue.of(int[].class), equalTo(new int[1]));
        assertThat(NonDefaultValue.of(String.class), equalTo("String"));
        assertThat(NonDefaultValue.of(Object.class), notNullValue());
        assertThat(NonDefaultValue.of(OrthogonalInterface.class), instanceOf(OrthogonalInterface.class));
        assertThat(NonDefaultValue.of(PublicEnum.class), instanceOf(PublicEnum.class));
        assertThat(NonDefaultValue.of(EmptyEnum.class), nullValue());
        assertThat(NonDefaultValue.of(Simple.class), instanceOf(Simple.class));
    }

    @Test
    public void testGetDescription() throws Exception {
        assertThat(NonDefaultValue.INSTANCE.getDescription(boolean.class), equalTo("true"));
        assertThat(NonDefaultValue.INSTANCE.getDescription(char.class), equalTo("'\\u0001'"));
        assertThat(NonDefaultValue.INSTANCE.getDescription(byte.class), equalTo("(byte) 1"));
        assertThat(NonDefaultValue.INSTANCE.getDescription(short.class), equalTo("(short) 1"));
        assertThat(NonDefaultValue.INSTANCE.getDescription(int.class), equalTo("1"));
        assertThat(NonDefaultValue.INSTANCE.getDescription(float.class), equalTo("1.0f"));
        assertThat(NonDefaultValue.INSTANCE.getDescription(long.class), equalTo("1l"));
        assertThat(NonDefaultValue.INSTANCE.getDescription(double.class), equalTo("1.0"));
        assertThat(NonDefaultValue.INSTANCE.getDescription(int[].class), equalTo("new int[1]"));
        assertThat(NonDefaultValue.INSTANCE.getDescription(String.class), equalTo("\"String\""));
        assertThat(NonNullValue.INSTANCE.getDescription(Object.class), equalTo("new Object()"));
        assertThat(NonDefaultValue.INSTANCE.getDescription(OrthogonalInterface.class), equalTo("proxy OrthogonalInterface()"));
        assertThat(NonDefaultValue.INSTANCE.getDescription(PublicEnum.class), equalTo("VALUE1"));
        assertThat(NonDefaultValue.INSTANCE.getDescription(EmptyEnum.class), equalTo("null"));
        assertThat(NonDefaultValue.INSTANCE.getDescription(Simple.class), equalTo("new Simple()"));
    }

}
