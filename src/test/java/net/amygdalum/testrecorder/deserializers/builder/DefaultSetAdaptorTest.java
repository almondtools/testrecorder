package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetAdaptorTest {

	private DefaultSetAdaptor adaptor;

	@Before
	public void before() throws Exception {
		adaptor = new DefaultSetAdaptor();
	}
	
	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent(), nullValue());
	}

	@Test
	public void testMatchesSets() throws Exception {
        assertThat(adaptor.matches(Object.class),is(false));
        assertThat(adaptor.matches(HashSet.class),is(true));
        assertThat(adaptor.matches(TreeSet.class),is(true));
        assertThat(adaptor.matches(Set.class),is(true));
        assertThat(adaptor.matches(SortedSet.class),is(true));
        assertThat(adaptor.matches(new HashSet<Object>() {}.getClass()),is(true));
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(LinkedHashSet.class, null, Integer.class)).withResult(parameterized(Set.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements().toString(), allOf(
			containsString("LinkedHashSet<Integer> temp1 = new LinkedHashSet<Integer>()"),
			containsString("temp1.add(0)"),
			containsString("temp1.add(8)"),
			containsString("temp1.add(15)"),
			containsString("Set<Integer> set1 = temp1;")));
		assertThat(result.getValue(), equalTo("set1"));
	}

	@Test
	public void testTryDeserializeSameResultTypes() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(LinkedHashSet.class, null, Integer.class)).withResult(parameterized(LinkedHashSet.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements().toString(), allOf(
			containsString("LinkedHashSet<Integer> set1 = new LinkedHashSet<Integer>()"),
			containsString("set1.add(0)"),
			containsString("set1.add(8)"),
			containsString("set1.add(15)")));
		assertThat(result.getValue(), equalTo("set1"));
	}

    @Test
    public void testTryDeserializeNonListResult() throws Exception {
        SerializedSet value = new SerializedSet(parameterized(PublicSet.class, null, Integer.class))
            .withResult(AnInterface.class);
        value.add(literal(0));
        value.add(literal(8));
        value.add(literal(15));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), allOf(
            containsString("PublicSet<Integer> temp1 = new PublicSet<Integer>()"), 
            containsString("temp1.add(0)"),
            containsString("temp1.add(8)"),
            containsString("temp1.add(15)"),
            containsString("AnInterface set1 = temp1;")));
        assertThat(result.getValue(), equalTo("set1"));
    }

    @Test
    public void testTryDeserializeNeedingAdaptation() throws Exception {
        SerializedSet value = new SerializedSet(parameterized(PrivateSet.class, null, Integer.class))
            .withResult(AnInterface.class);
        value.add(literal(0));
        value.add(literal(8));
        value.add(literal(15));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), allOf(
            containsString("java.util.Set temp1 = (java.util.Set) clazz(\"net.amygdalum.testrecorder.deserializers.builder.DefaultSetAdaptorTest$PrivateSet\").value();"),
            containsString("temp1.add(0)"),
            containsString("temp1.add(8)"),
            containsString("temp1.add(15)"),
            containsString("AnInterface set1 = (AnInterface) temp1;")));
        assertThat(result.getValue(), equalTo("set1"));
    }

    @Test
    public void testTryDeserializeNeedingHiddenAdaptation() throws Exception {
        SerializedSet value = new SerializedSet(parameterized(PrivateSet.class, null, Integer.class)).withResult(parameterized(LinkedHashSet.class, null, Integer.class));
        value.add(literal(0));
        value.add(literal(8));
        value.add(literal(15));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), not(containsString("new net.amygdalum.testrecorder.deserializers.builder.DefaultSetAdaptorTest$PrivateSet"))); 
        assertThat(result.getStatements().toString(), allOf(
            containsString("LinkedHashSet<Integer> set1 = (LinkedHashSet<Integer>) clazz(\"net.amygdalum.testrecorder.deserializers.builder.DefaultSetAdaptorTest$PrivateSet\").value();"), 
            containsString("set1.add(0)"),
            containsString("set1.add(8)"),
            containsString("set1.add(15)")));
        assertThat(result.getValue(), equalTo("set1"));
    }

    public interface AnInterface {
        
    }
    
    private static class PrivateSet<T> extends LinkedHashSet<T> implements AnInterface {

    }
    
    public static class PublicSet<T> extends LinkedHashSet<T> implements AnInterface {

    }
    
}
