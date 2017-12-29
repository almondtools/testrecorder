package net.amygdalum.testrecorder.evaluator;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.ConfigurableSerializerFacade;
import net.amygdalum.testrecorder.DefaultTestRecorderAgentConfig;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class SerializedValueEvaluatorTest {

    private ConfigurableSerializerFacade facade;

    @BeforeEach
    public void before() throws Exception {
        facade = new ConfigurableSerializerFacade(new DefaultTestRecorderAgentConfig());
    }

    @Test
    public void testEvaluateLiteralFails() throws Exception {
        SerializedValue value = facade.serialize(String.class, "str");

        assertThat(new SerializedValueEvaluator(".str").applyTo(value).isPresent(), is(false));
        assertThat(new SerializedValueEvaluator("[0]").applyTo(value).isPresent(), is(false));
    }

    @Test
    public void testEvaluateField() throws Exception {
        SerializedValue value = facade.serialize(Simple.class, new Simple("strValue"));

        assertThat(new SerializedValueEvaluator(".str").applyTo(value).get().toString(), equalTo("strValue"));
    }

    @Test
    public void testEvaluateFieldFails() throws Exception {
        SerializedValue value = facade.serialize(Simple.class, new Simple("strValue"));
        SerializedValue nullValue = facade.serialize(Simple.class, null);
        

        assertThat(new SerializedValueEvaluator(".s").applyTo(value).isPresent(), is(false));
        assertThat(new SerializedValueEvaluator(".str").applyTo(nullValue).isPresent(), is(false));
    }

    @Test
    public void testEvaluateArray() throws Exception {
        SerializedValue value = facade.serialize(String[].class, new String[]{"foo", "bar"});

        assertThat(new SerializedValueEvaluator("[0]").applyTo(value).get().toString(), equalTo("foo"));
        assertThat(new SerializedValueEvaluator("[1]").applyTo(value).get().toString(), equalTo("bar"));
    }

    @Test
    public void testEvaluateArrayFails() throws Exception {
        SerializedValue value = facade.serialize(String[].class, new String[]{"foo", "bar"});
        
        assertThat(new SerializedValueEvaluator("[2]").applyTo(value).isPresent(), is(false));
        assertThat(new SerializedValueEvaluator("[-1]").applyTo(value).isPresent(), is(false));
        assertThat(new SerializedValueEvaluator("[str]").applyTo(value).isPresent(), is(false));
    }
    
    @Test
    public void testEvaluateList() throws Exception {
        SerializedValue value = facade.serialize(List.class, asList("bar","foo"));
        
        assertThat(new SerializedValueEvaluator("[0]").applyTo(value).get().toString(), equalTo("bar"));
        assertThat(new SerializedValueEvaluator("[1]").applyTo(value).get().toString(), equalTo("foo"));
    }

    @Test
    public void testEvaluateListFails() throws Exception {
        SerializedValue value = facade.serialize(List.class, asList("bar","foo"));
        
        assertThat(new SerializedValueEvaluator("[2]").applyTo(value).isPresent(), is(false));
        assertThat(new SerializedValueEvaluator("[-1]").applyTo(value).isPresent(), is(false));
        assertThat(new SerializedValueEvaluator("[str]").applyTo(value).isPresent(), is(false));
    }
    
    @Test
    public void testEvaluateNestedField() throws Exception {
        SerializedValue value = facade.serialize(Complex.class, new Complex("sstr"));
        
        assertThat(new SerializedValueEvaluator(".simple.str").applyTo(value).get().toString(), equalTo("sstr"));
    }

}
