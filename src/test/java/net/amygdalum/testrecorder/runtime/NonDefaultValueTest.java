package net.amygdalum.testrecorder.runtime;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.EmptyEnum;
import net.amygdalum.testrecorder.util.testobjects.OrthogonalInterface;
import net.amygdalum.testrecorder.util.testobjects.PublicEnum;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class NonDefaultValueTest {

    @Test
    public void testGetNonDefaultValue() throws Exception {
        assertThat(NonDefaultValue.of(boolean.class)).isEqualTo(true);
        assertThat(NonDefaultValue.of(char.class)).isEqualTo((char) 1);
        assertThat(NonDefaultValue.of(byte.class)).isEqualTo((byte) 1);
        assertThat(NonDefaultValue.of(short.class)).isEqualTo((short) 1);
        assertThat(NonDefaultValue.of(int.class)).isEqualTo((int) 1);
        assertThat(NonDefaultValue.of(float.class)).isEqualTo((float) 1);
        assertThat(NonDefaultValue.of(long.class)).isEqualTo((long) 1);
        assertThat(NonDefaultValue.of(double.class)).isEqualTo((double) 1);
        assertThat(NonDefaultValue.of(int[].class)).isEqualTo(new int[1]);
        assertThat(NonDefaultValue.of(String.class)).isEqualTo("String");
        assertThat(NonDefaultValue.of(Object.class)).isNotNull();
        assertThat(NonDefaultValue.of(OrthogonalInterface.class)).isInstanceOf(OrthogonalInterface.class);
        assertThat(NonDefaultValue.of(PublicEnum.class)).isInstanceOf(PublicEnum.class);
        assertThat(NonDefaultValue.of(EmptyEnum.class)).isNull();
        assertThat(NonDefaultValue.of(Simple.class)).isInstanceOf(Simple.class);
    }

    @Test
    public void testGetDescription() throws Exception {
        assertThat(NonDefaultValue.INSTANCE.getDescription(boolean.class)).isEqualTo("true");
        assertThat(NonDefaultValue.INSTANCE.getDescription(char.class)).isEqualTo("'\\u0001'");
        assertThat(NonDefaultValue.INSTANCE.getDescription(byte.class)).isEqualTo("(byte) 1");
        assertThat(NonDefaultValue.INSTANCE.getDescription(short.class)).isEqualTo("(short) 1");
        assertThat(NonDefaultValue.INSTANCE.getDescription(int.class)).isEqualTo("1");
        assertThat(NonDefaultValue.INSTANCE.getDescription(float.class)).isEqualTo("1.0f");
        assertThat(NonDefaultValue.INSTANCE.getDescription(long.class)).isEqualTo("1l");
        assertThat(NonDefaultValue.INSTANCE.getDescription(double.class)).isEqualTo("1.0");
        assertThat(NonDefaultValue.INSTANCE.getDescription(int[].class)).isEqualTo("new int[1]");
        assertThat(NonDefaultValue.INSTANCE.getDescription(String.class)).isEqualTo("\"String\"");
        assertThat(NonNullValue.INSTANCE.getDescription(Object.class)).isEqualTo("new Object()");
        assertThat(NonDefaultValue.INSTANCE.getDescription(OrthogonalInterface.class)).isEqualTo("proxy OrthogonalInterface()");
        assertThat(NonDefaultValue.INSTANCE.getDescription(PublicEnum.class)).isEqualTo("VALUE1");
        assertThat(NonDefaultValue.INSTANCE.getDescription(EmptyEnum.class)).isEqualTo("null");
        assertThat(NonDefaultValue.INSTANCE.getDescription(Simple.class)).isEqualTo("new Simple()");
    }

}
