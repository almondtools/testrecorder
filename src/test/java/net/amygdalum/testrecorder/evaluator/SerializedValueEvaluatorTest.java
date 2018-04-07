package net.amygdalum.testrecorder.evaluator;

import static java.util.Arrays.asList;
import static net.amygdalum.testrecorder.TestAgentConfiguration.defaultConfig;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.ConfigurableSerializerFacade;
import net.amygdalum.testrecorder.profile.AgentConfiguration;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.testobjects.Complex;
import net.amygdalum.testrecorder.util.testobjects.Simple;

public class SerializedValueEvaluatorTest {

	private AgentConfiguration config;
    private ConfigurableSerializerFacade facade;

    @BeforeEach
    public void before() throws Exception {
        config = defaultConfig();
		facade = new ConfigurableSerializerFacade(config);
    }

    @Test
    public void testEvaluateLiteralFails() throws Exception {
        SerializedValue value = facade.serialize(String.class, "str");

        assertThat(new SerializedValueEvaluator(".str").applyTo(value).isPresent()).isFalse();
        assertThat(new SerializedValueEvaluator("[0]").applyTo(value).isPresent()).isFalse();
    }

    @Test
    public void testEvaluateField() throws Exception {
        SerializedValue value = facade.serialize(Simple.class, new Simple("strValue"));

        assertThat(new SerializedValueEvaluator(".str").applyTo(value).get().toString()).isEqualTo("strValue");
    }

    @Test
    public void testEvaluateFieldFails() throws Exception {
        SerializedValue value = facade.serialize(Simple.class, new Simple("strValue"));
        SerializedValue nullValue = facade.serialize(Simple.class, null);
        

        assertThat(new SerializedValueEvaluator(".s").applyTo(value).isPresent()).isFalse();
        assertThat(new SerializedValueEvaluator(".str").applyTo(nullValue).isPresent()).isFalse();
    }

    @Test
    public void testEvaluateArray() throws Exception {
        SerializedValue value = facade.serialize(String[].class, new String[]{"foo", "bar"});

        assertThat(new SerializedValueEvaluator("[0]").applyTo(value).get().toString()).isEqualTo("foo");
        assertThat(new SerializedValueEvaluator("[1]").applyTo(value).get().toString()).isEqualTo("bar");
    }

    @Test
    public void testEvaluateArrayFails() throws Exception {
        SerializedValue value = facade.serialize(String[].class, new String[]{"foo", "bar"});
        
        assertThat(new SerializedValueEvaluator("[2]").applyTo(value).isPresent()).isFalse();
        assertThat(new SerializedValueEvaluator("[-1]").applyTo(value).isPresent()).isFalse();
        assertThat(new SerializedValueEvaluator("[str]").applyTo(value).isPresent()).isFalse();
    }
    
    @Test
    public void testEvaluateList() throws Exception {
        SerializedValue value = facade.serialize(List.class, asList("bar","foo"));
        
        assertThat(new SerializedValueEvaluator("[0]").applyTo(value).get().toString()).isEqualTo("bar");
        assertThat(new SerializedValueEvaluator("[1]").applyTo(value).get().toString()).isEqualTo("foo");
    }

    @Test
    public void testEvaluateListFails() throws Exception {
        SerializedValue value = facade.serialize(List.class, asList("bar","foo"));
        
        assertThat(new SerializedValueEvaluator("[2]").applyTo(value).isPresent()).isFalse();
        assertThat(new SerializedValueEvaluator("[-1]").applyTo(value).isPresent()).isFalse();
        assertThat(new SerializedValueEvaluator("[str]").applyTo(value).isPresent()).isFalse();
    }
    
    @Test
    public void testEvaluateNestedField() throws Exception {
        SerializedValue value = facade.serialize(Complex.class, new Complex("sstr"));
        
        assertThat(new SerializedValueEvaluator(".simple.str").applyTo(value).get().toString()).isEqualTo("sstr");
    }

}
