package net.amygdalum.testrecorder.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DefaultValueTest {

    @Test
    public void testGetDefaultValue() throws Exception {
        assertThat(DefaultValue.of(byte.class), equalTo(Byte.valueOf((byte) 0)));
        assertThat(DefaultValue.of(short.class), equalTo(Short.valueOf((short) 0)));
        assertThat(DefaultValue.of(int.class), equalTo(Integer.valueOf(0)));
        assertThat(DefaultValue.of(long.class), equalTo(Long.valueOf(0)));
        assertThat(DefaultValue.of(float.class), equalTo(Float.valueOf(0)));
        assertThat(DefaultValue.of(double.class), equalTo(Double.valueOf(0)));
        assertThat(DefaultValue.of(boolean.class), equalTo(Boolean.valueOf("false")));
        assertThat(DefaultValue.of(char.class), equalTo(Character.valueOf((char) 0)));
        assertThat(DefaultValue.of(String.class), nullValue());
        assertThat(DefaultValue.of(AnEnum.class), nullValue());
        assertThat(DefaultValue.of(AnInterface.class), nullValue());
        assertThat(DefaultValue.of(Object.class), nullValue());
        assertThat(DefaultValue.of(Simple.class), nullValue());
    }

    @Test
    public void testGetDescription() throws Exception {
        assertThat(DefaultValue.INSTANCE.getDescription(boolean.class), equalTo("false"));
        assertThat(DefaultValue.INSTANCE.getDescription(char.class), equalTo("'\\u0000'"));
        assertThat(DefaultValue.INSTANCE.getDescription(byte.class), equalTo("(byte) 0"));
        assertThat(DefaultValue.INSTANCE.getDescription(short.class), equalTo("(short) 0"));
        assertThat(DefaultValue.INSTANCE.getDescription(int.class), equalTo("0"));
        assertThat(DefaultValue.INSTANCE.getDescription(float.class), equalTo("0.0f"));
        assertThat(DefaultValue.INSTANCE.getDescription(long.class), equalTo("0l"));
        assertThat(DefaultValue.INSTANCE.getDescription(double.class), equalTo("0.0"));
        assertThat(DefaultValue.INSTANCE.getDescription(int[].class), equalTo("null"));
        assertThat(DefaultValue.INSTANCE.getDescription(String.class), equalTo("null"));
        assertThat(DefaultValue.INSTANCE.getDescription(Object.class), equalTo("null"));
        assertThat(DefaultValue.INSTANCE.getDescription(AnInterface.class), equalTo("null"));
        assertThat(DefaultValue.INSTANCE.getDescription(AnEnum.class), equalTo("null"));
        assertThat(DefaultValue.INSTANCE.getDescription(Simple.class), equalTo("null"));
    }

    private interface AnInterface {
        
    }

    private enum AnEnum {
        ENUM;
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
