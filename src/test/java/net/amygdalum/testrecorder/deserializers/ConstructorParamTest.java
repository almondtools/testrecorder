package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Constructor;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.builder.SetupGenerators;
import net.amygdalum.testrecorder.util.testobjects.Simple;
import net.amygdalum.testrecorder.values.SerializedField;

public class ConstructorParamTest {

    private Constructor<Simple> constructor;
    private ConstructorParam constructorParam;

    @Before
    public void before() throws Exception {
        constructor = Simple.class.getDeclaredConstructor(String.class);
        constructorParam = new ConstructorParam(constructor, 0, new SerializedField(Simple.class, "field", String.class, literal("value")), "value");
    }

    @Test
    public void testConstructorParam() throws Exception {
        assertThat(constructorParam.getField().getName(), equalTo("field"));
        assertThat(constructorParam.getParamNumber(), equalTo(0));
        
        assertThat(constructorParam.computeSerializedValue(), equalTo(literal("value")));
        assertThat(constructorParam.getValue(), equalTo("value"));
    }

    @Test
    public void testConstructorParamWithoutField() throws Exception {
        assertThat(new ConstructorParam(constructor, 0).computeSerializedValue(), equalTo(nullInstance(null)));
        assertThat(new ConstructorParam(constructor, 0).assertType(String.class).computeSerializedValue(), equalTo(nullInstance(String.class)));
        assertThat(new ConstructorParam(constructor, 0).assertType(Object.class).computeSerializedValue(), equalTo(nullInstance(Object.class)));
        assertThat(new ConstructorParam(constructor, 0).assertType(int.class).computeSerializedValue(), equalTo(literal(0)));
    }

    @Test
    public void testToString() throws Exception {
        assertThat(constructorParam.toString(), equalTo("public net.amygdalum.testrecorder.util.testobjects.Simple(java.lang.String):0=value=> field"));
    }

    @Test
    public void testCompile() throws Exception {
        SetupGenerators compiler = new SetupGenerators(Simple.class);
        TypeManager types = new TypeManager();
        
        assertThat(new ConstructorParam(constructor, 0, new SerializedField(Simple.class, "field", String.class, literal("value")), "value")
            .compile(types , compiler).getValue(), equalTo("\"value\""));
        assertThat(new ConstructorParam(constructor, 0)
            .assertType(String.class)
            .compile(types , compiler).getValue(), equalTo("null"));
        assertThat(new ConstructorParam(constructor, 0)
            .insertTypeCasts()
            .assertType(String.class)
            .compile(types , compiler).getValue(), equalTo("(String) null"));
        assertThat(new ConstructorParam(constructor, 0, new SerializedField(Simple.class, "field", String.class, literal("value")), "value")
            .assertType(Integer.class)
            .compile(types , compiler).getValue(), equalTo("(Integer) \"value\""));
        assertThat(new ConstructorParam(constructor, 0, new SerializedField(Simple.class, "field", String.class, literal("value")), "value")
            .assertType(String.class)
            .compile(types , compiler).getValue(), equalTo("\"value\""));
        assertThat(new ConstructorParam(constructor, 0, new SerializedField(Simple.class, "field", String.class, nullInstance(String.class)), null)
            .assertType(String.class)
            .compile(types , compiler).getValue(), equalTo("null"));
    }

}