package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.values.SerializedField;

public class SetterParamTest {

    private Method method;
    private SetterParam setterParam;

    @Before
    public void before() throws Exception {
        method = TestObject.class.getDeclaredMethod("setField", String.class);
        setterParam = new SetterParam(method, new SerializedField(TestObject.class, "field", String.class, literal("value")), "value");

    }

    @Test
    public void testSetterParam() throws Exception {
        assertThat(setterParam.getField().getName(), equalTo("field"));
        assertThat(setterParam.getName(), equalTo("setField"));
        assertThat(setterParam.getValue(), equalTo("value"));
        assertThat(setterParam.computeSerializedValue(), equalTo(literal("value")));
    }

    @Test
    public void testApply() throws Exception {
        TestObject object = new TestObject();
        setterParam.apply(object);
        
        assertThat(object.getField(), equalTo("value"));
    }

    @Test
    public void testToString() throws Exception {
        assertThat(setterParam.toString(), equalTo("public void net.amygdalum.testrecorder.deserializers.SetterParamTest$TestObject.setField(java.lang.String)=value=> field"));
    }

    public static class TestObject {
        private String field;

        public void setField(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }
    }

}
