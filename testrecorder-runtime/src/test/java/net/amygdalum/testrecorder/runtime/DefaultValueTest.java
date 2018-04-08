package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.OrthogonalInterface;
import net.amygdalum.testrecorder.util.testobjects.PublicEnum;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class DefaultValueTest {

    @Test
    public void testGetDefaultValue() throws Exception {
        assertThat(DefaultValue.of(byte.class)).isEqualTo(Byte.valueOf((byte) 0));
        assertThat(DefaultValue.of(short.class)).isEqualTo(Short.valueOf((short) 0));
        assertThat(DefaultValue.of(int.class)).isEqualTo(Integer.valueOf(0));
        assertThat(DefaultValue.of(long.class)).isEqualTo(Long.valueOf(0));
        assertThat(DefaultValue.of(float.class)).isEqualTo(Float.valueOf(0));
        assertThat(DefaultValue.of(double.class)).isEqualTo(Double.valueOf(0));
        assertThat(DefaultValue.of(boolean.class)).isEqualTo(Boolean.valueOf("false"));
        assertThat(DefaultValue.of(char.class)).isEqualTo(Character.valueOf((char) 0));
        assertThat(DefaultValue.of(String.class)).isNull();
        assertThat(DefaultValue.of(PublicEnum.class)).isNull();
        assertThat(DefaultValue.of(OrthogonalInterface.class)).isNull();
        assertThat(DefaultValue.of(Object.class)).isNull();
        assertThat(DefaultValue.of(Simple.class)).isNull();
    }

    @Test
    public void testGetDescription() throws Exception {
        assertThat(DefaultValue.INSTANCE.getDescription(boolean.class)).isEqualTo("false");
        assertThat(DefaultValue.INSTANCE.getDescription(char.class)).isEqualTo("'\\u0000'");
        assertThat(DefaultValue.INSTANCE.getDescription(byte.class)).isEqualTo("(byte) 0");
        assertThat(DefaultValue.INSTANCE.getDescription(short.class)).isEqualTo("(short) 0");
        assertThat(DefaultValue.INSTANCE.getDescription(int.class)).isEqualTo("0");
        assertThat(DefaultValue.INSTANCE.getDescription(float.class)).isEqualTo("0.0f");
        assertThat(DefaultValue.INSTANCE.getDescription(long.class)).isEqualTo("0l");
        assertThat(DefaultValue.INSTANCE.getDescription(double.class)).isEqualTo("0.0");
        assertThat(DefaultValue.INSTANCE.getDescription(int[].class)).isEqualTo("null");
        assertThat(DefaultValue.INSTANCE.getDescription(String.class)).isEqualTo("null");
        assertThat(DefaultValue.INSTANCE.getDescription(Object.class)).isEqualTo("null");
        assertThat(DefaultValue.INSTANCE.getDescription(OrthogonalInterface.class)).isEqualTo("null");
        assertThat(DefaultValue.INSTANCE.getDescription(PublicEnum.class)).isEqualTo("null");
        assertThat(DefaultValue.INSTANCE.getDescription(Simple.class)).isEqualTo("null");
    }

}
