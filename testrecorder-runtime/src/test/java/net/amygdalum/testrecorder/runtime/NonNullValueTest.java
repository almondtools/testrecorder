package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.EmptyEnum;
import net.amygdalum.testrecorder.util.testobjects.OrthogonalInterface;
import net.amygdalum.testrecorder.util.testobjects.PublicEnum;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class NonNullValueTest {

    @Test
    public void testGetNonNullValue() throws Exception {
        assertThat(NonNullValue.of(byte.class)).isEqualTo(Byte.valueOf((byte) 0));
        assertThat(NonNullValue.of(short.class)).isEqualTo(Short.valueOf((short) 0));
        assertThat(NonNullValue.of(int.class)).isEqualTo(Integer.valueOf(0));
        assertThat(NonNullValue.of(long.class)).isEqualTo(Long.valueOf(0));
        assertThat(NonNullValue.of(float.class)).isEqualTo(Float.valueOf(0));
        assertThat(NonNullValue.of(double.class)).isEqualTo(Double.valueOf(0));
        assertThat(NonNullValue.of(boolean.class)).isEqualTo(Boolean.valueOf("false"));
        assertThat(NonNullValue.of(char.class)).isEqualTo(Character.valueOf((char) 0));
        assertThat(NonNullValue.of(String.class)).isEqualTo("");
        assertThat(NonNullValue.of(int[].class)).isEqualTo(new int[0]);
        assertThat(NonNullValue.of(OrthogonalInterface.class)).isInstanceOf(OrthogonalInterface.class);
        assertThat(NonNullValue.of(PublicEnum.class)).isInstanceOf(PublicEnum.class);
        assertThat(NonNullValue.of(EmptyEnum.class)).isNull();
        assertThat(NonNullValue.of(Object.class)).isNotNull();
        assertThat(NonNullValue.of(Simple.class)).isInstanceOf(Simple.class);
    }

    @Test
    public void testGetDescription() throws Exception {
        assertThat(NonNullValue.INSTANCE.getDescription(boolean.class)).isEqualTo("false");
        assertThat(NonNullValue.INSTANCE.getDescription(char.class)).isEqualTo("'\\u0000'");
        assertThat(NonNullValue.INSTANCE.getDescription(byte.class)).isEqualTo("(byte) 0");
        assertThat(NonNullValue.INSTANCE.getDescription(short.class)).isEqualTo("(short) 0");
        assertThat(NonNullValue.INSTANCE.getDescription(int.class)).isEqualTo("0");
        assertThat(NonNullValue.INSTANCE.getDescription(float.class)).isEqualTo("0.0f");
        assertThat(NonNullValue.INSTANCE.getDescription(long.class)).isEqualTo("0l");
        assertThat(NonNullValue.INSTANCE.getDescription(double.class)).isEqualTo("0.0");
        assertThat(NonNullValue.INSTANCE.getDescription(int[].class)).isEqualTo("new int[0]");
        assertThat(NonNullValue.INSTANCE.getDescription(String.class)).isEqualTo("\"\"");
        assertThat(NonNullValue.INSTANCE.getDescription(Object.class)).isEqualTo("new Object()");
        assertThat(NonNullValue.INSTANCE.getDescription(OrthogonalInterface.class)).isEqualTo("proxy OrthogonalInterface()");
        assertThat(NonNullValue.INSTANCE.getDescription(PublicEnum.class)).isEqualTo("VALUE1");
        assertThat(NonNullValue.INSTANCE.getDescription(Simple.class)).isEqualTo("new Simple()");
    }
    
}
