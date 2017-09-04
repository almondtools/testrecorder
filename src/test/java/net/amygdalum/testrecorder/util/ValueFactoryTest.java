package net.amygdalum.testrecorder.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.amygdalum.testrecorder.util.ValueFactory;

public class ValueFactoryTest {

    @Test
    public void testNONE() throws Exception {
        assertThat(ValueFactory.NONE.newValue(Object.class), nullValue());
    }

    @Test
    public void testGetDescriptionNull() throws Exception {
        assertThat(ValueFactory.NONE.getDescription(Object.class), equalTo("null"));
        assertThat(new ValueFactory() {

            @Override
            public Object newValue(Class<?> clazz) {
                return new Object() {
                    @Override
                    public String toString() {
                        throw new RuntimeException();
                    }
                };
            }
        }.getDescription(Object.class), equalTo("<undescribable>"));
    }
}
