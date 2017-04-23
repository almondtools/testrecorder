package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.classOfHiddenMap;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.util.testobjects.OrthogonalInterface;
import net.amygdalum.testrecorder.util.testobjects.PublicMap;
import net.amygdalum.testrecorder.values.SerializedMap;

public class DefaultMapAdaptorTest {

    private DefaultMapAdaptor adaptor;

    @Before
    public void before() throws Exception {
        adaptor = new DefaultMapAdaptor();
    }

    @Test
    public void testParentNull() throws Exception {
        assertThat(adaptor.parent(), nullValue());
    }

    @Test
    public void testMatchesAnyArray() throws Exception {
        assertThat(adaptor.matches(Object.class),is(false));
        assertThat(adaptor.matches(HashMap.class),is(true));
        assertThat(adaptor.matches(LinkedHashMap.class),is(true));
        assertThat(adaptor.matches(Map.class),is(true));
        assertThat(adaptor.matches(new LinkedHashMap<Object, Object>(){}.getClass()),is(true));
    }

    @Test
    public void testTryDeserialize() throws Exception {
        SerializedMap value = new SerializedMap(parameterized(LinkedHashMap.class, null, Integer.class, Integer.class)).withResult(parameterized(Map.class, null, Integer.class, Integer.class));
        value.put(literal(8), literal(15));
        value.put(literal(47), literal(11));
        SetupGenerators generator = new SetupGenerators(getClass());

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), allOf(
            containsString("LinkedHashMap<Integer, Integer> temp1 = new LinkedHashMap<Integer, Integer>()"),
            containsString("temp1.put(8, 15)"),
            containsString("temp1.put(47, 11)"),
            containsString("Map<Integer, Integer> map1 = temp1;")));
        assertThat(result.getValue(), equalTo("map1"));
    }

    @Test
    public void testTryDeserializeSameResultTypes() throws Exception {
        SerializedMap value = new SerializedMap(parameterized(LinkedHashMap.class, null, Integer.class, Integer.class))
            .withResult(parameterized(LinkedHashMap.class, null, Integer.class, Integer.class));
        value.put(literal(8), literal(15));
        value.put(literal(47), literal(11));
        SetupGenerators generator = new SetupGenerators(getClass());

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), allOf(
            containsString("LinkedHashMap<Integer, Integer> map1 = new LinkedHashMap<Integer, Integer>()"),
            containsString("map1.put(8, 15)"),
            containsString("map1.put(47, 11)")));
        assertThat(result.getValue(), equalTo("map1"));
    }

    @Test
    public void testTryDeserializeNonListResult() throws Exception {
        SerializedMap value = new SerializedMap(parameterized(PublicMap.class, null, Integer.class, Integer.class))
            .withResult(OrthogonalInterface.class);
        value.put(literal(8),literal(15));
        value.put(literal(47), literal(11));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), allOf(
            containsString("PublicMap<Integer, Integer> temp1 = new PublicMap<Integer, Integer>()"), 
            containsString("temp1.put(8, 15)"),
            containsString("temp1.put(47, 11)"),
            containsString("OrthogonalInterface map1 = temp1;")));
        assertThat(result.getValue(), equalTo("map1"));
    }

    @Test
    public void testTryDeserializeNeedingAdaptation() throws Exception {
        SerializedMap value = new SerializedMap(parameterized(classOfHiddenMap(), null, Integer.class, Integer.class))
            .withResult(OrthogonalInterface.class);
        value.put(literal(8),literal(15));
        value.put(literal(47), literal(11));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), allOf(
            containsString("Map temp1 = (Map) clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenMap\").value();"),
            containsString("temp1.put(8, 15)"),
            containsString("temp1.put(47, 11)"),
            containsString("OrthogonalInterface map1 = (OrthogonalInterface) temp1;")));
        assertThat(result.getValue(), equalTo("map1"));
    }
    
    @Test
    public void testTryDeserializeHiddenType() throws Exception {
        SerializedMap value = new SerializedMap(parameterized(classOfHiddenMap(), null, Integer.class, Integer.class)).withResult(parameterized(LinkedHashMap.class, null, Integer.class,Integer.class));
        value.put(literal(8),literal(15));
        value.put(literal(47), literal(11));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), not(containsString("new net.amygdalum.testrecorder.util.testobjects.Hidden.HiddenMap"))); 
        assertThat(result.getStatements().toString(), allOf(
            containsString("LinkedHashMap<Integer, Integer> map1 = (LinkedHashMap<Integer, Integer>) clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenMap\").value();"),
            containsString("map1.put(8, 15)"),
            containsString("map1.put(47, 11)")));
        assertThat(result.getValue(), equalTo("map1"));
    }
    
}
