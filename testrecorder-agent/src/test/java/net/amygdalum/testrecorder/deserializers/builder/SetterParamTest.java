package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Bean;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.values.SerializedField;

public class SetterParamTest {

    private Method method;
    private SetterParam setterParam;

    @BeforeEach
    public void before() throws Exception {
        method = Bean.class.getDeclaredMethod("setAttribute", String.class);
        setterParam = new SetterParam(method, String.class, new SerializedField(Simple.class, "attribute", String.class, literal("value")), "value");
    }

    @Test
    public void testSetterParam() throws Exception {
        assertThat(setterParam.getField().getName()).isEqualTo("attribute");
        assertThat(setterParam.getName()).isEqualTo("setAttribute");
        assertThat(setterParam.getValue()).isEqualTo("value");
        assertThat(setterParam.computeSerializedValue()).isEqualTo(literal("value"));
    }

    @Test
    public void testApply() throws Exception {
        Bean object = new Bean();
        setterParam.apply(object);
        
        assertThat(object.getAttribute()).isEqualTo("value");
    }

    @Test
    public void testToString() throws Exception {
        assertThat(setterParam.toString()).isEqualTo("public void net.amygdalum.testrecorder.util.testobjects.Bean.setAttribute(java.lang.String)=value=> attribute");
    }

}
