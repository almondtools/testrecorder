package net.amygdalum.testrecorder.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class NonNullValueTest {

    @Test
    public void testGetNonNullValue() throws Exception {
        assertThat(NonNullValue.of(byte.class), equalTo(Byte.valueOf((byte) 0)));
        assertThat(NonNullValue.of(short.class), equalTo(Short.valueOf((short) 0)));
        assertThat(NonNullValue.of(int.class), equalTo(Integer.valueOf(0)));
        assertThat(NonNullValue.of(long.class), equalTo(Long.valueOf(0)));
        assertThat(NonNullValue.of(float.class), equalTo(Float.valueOf(0)));
        assertThat(NonNullValue.of(double.class), equalTo(Double.valueOf(0)));
        assertThat(NonNullValue.of(boolean.class), equalTo(Boolean.valueOf("false")));
        assertThat(NonNullValue.of(char.class), equalTo(Character.valueOf((char) 0)));
        assertThat(NonNullValue.of(String.class), equalTo(""));
        assertThat(NonNullValue.of(int[].class), equalTo(new int[0]));
        assertThat(NonNullValue.of(AnInterface.class), instanceOf(AnInterface.class));
        assertThat(NonNullValue.of(AnEnum.class), instanceOf(AnEnum.class));
        assertThat(NonNullValue.of(EmptyEnum.class), nullValue());
        assertThat(NonNullValue.of(Object.class), notNullValue());
        assertThat(NonNullValue.of(Simple.class), instanceOf(Simple.class));
    }

    @Test
    public void testGetDescription() throws Exception {
        assertThat(NonNullValue.INSTANCE.getDescription(boolean.class), equalTo("false"));
        assertThat(NonNullValue.INSTANCE.getDescription(char.class), equalTo("'\\u0000'"));
        assertThat(NonNullValue.INSTANCE.getDescription(byte.class), equalTo("(byte) 0"));
        assertThat(NonNullValue.INSTANCE.getDescription(short.class), equalTo("(short) 0"));
        assertThat(NonNullValue.INSTANCE.getDescription(int.class), equalTo("0"));
        assertThat(NonNullValue.INSTANCE.getDescription(float.class), equalTo("0.0f"));
        assertThat(NonNullValue.INSTANCE.getDescription(long.class), equalTo("0l"));
        assertThat(NonNullValue.INSTANCE.getDescription(double.class), equalTo("0.0"));
        assertThat(NonNullValue.INSTANCE.getDescription(int[].class), equalTo("new int[0]"));
        assertThat(NonNullValue.INSTANCE.getDescription(String.class), equalTo("\"\""));
        assertThat(NonNullValue.INSTANCE.getDescription(Object.class), equalTo("new Object()"));
        assertThat(NonNullValue.INSTANCE.getDescription(AnInterface.class), equalTo("proxy AnInterface()"));
        assertThat(NonNullValue.INSTANCE.getDescription(AnEnum.class), equalTo("ENUM"));
        assertThat(NonNullValue.INSTANCE.getDescription(Simple.class), equalTo("new Simple()"));
    }
    
    private interface AnInterface {
    }

    private enum AnEnum {
        ENUM;
    }

    private enum EmptyEnum {
    }

    @SuppressWarnings("unused")
    private static class Simple {
        private String str;

        public Simple() {
        }

        public Simple(String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }
    }

}
