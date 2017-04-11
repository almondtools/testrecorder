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

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultQueueAdaptorTest {

	private DefaultQueueAdaptor adaptor;

	@Before
	public void before() throws Exception {
		adaptor = new DefaultQueueAdaptor();
	}
	
	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent(), nullValue());
	}

	@Test
	public void testMatchesLists() throws Exception {
		assertThat(adaptor.matches(Object.class),is(false));
        assertThat(adaptor.matches(LinkedList.class),is(true));
        assertThat(adaptor.matches(Queue.class),is(true));
        assertThat(adaptor.matches(Deque.class),is(true));
        assertThat(adaptor.matches(new LinkedList<Object>(){}.getClass()),is(true));
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedList value = new SerializedList(parameterized(LinkedList.class, null, Integer.class)).withResult(parameterized(List.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements().toString(), allOf(
			containsString("LinkedList<Integer> temp1 = new LinkedList<Integer>()"),
			containsString("temp1.add(0)"),
			containsString("temp1.add(8)"),
			containsString("temp1.add(15)"),
			containsString("List<Integer> queue1 = temp1;")));
		assertThat(result.getValue(), equalTo("queue1"));
	}
	
	@Test
	public void testTryDeserializeSameResultTypes() throws Exception {
		SerializedList value = new SerializedList(parameterized(LinkedList.class, null, Integer.class)).withResult(parameterized(LinkedList.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements().toString(), allOf(
			containsString("LinkedList<Integer> queue1 = new LinkedList<Integer>()"),
			containsString("queue1.add(0)"),
			containsString("queue1.add(8)"),
			containsString("queue1.add(15)")));
		assertThat(result.getValue(), equalTo("queue1"));
	}
	
    @Test
    public void testTryDeserializeNonListResult() throws Exception {
        SerializedList value = new SerializedList(parameterized(PublicList.class, null, Integer.class))
            .withResult(AnInterface.class);
        value.add(literal(0));
        value.add(literal(8));
        value.add(literal(15));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), allOf(
            containsString("PublicList<Integer> temp1 = new PublicList<Integer>()"), 
            containsString("temp1.add(0)"),
            containsString("temp1.add(8)"),
            containsString("temp1.add(15)"),
            containsString("AnInterface queue1 = temp1;")));
        assertThat(result.getValue(), equalTo("queue1"));
    }

    @Test
    public void testTryDeserializeNeedingAdaptation() throws Exception {
        SerializedList value = new SerializedList(parameterized(PrivateList.class, null, Integer.class))
            .withResult(AnInterface.class);
        value.add(literal(0));
        value.add(literal(8));
        value.add(literal(15));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), allOf(
            containsString("java.util.Queue temp1 = (java.util.Queue) clazz(\"net.amygdalum.testrecorder.deserializers.builder.DefaultQueueAdaptorTest$PrivateList\").value();"),
            containsString("temp1.add(0)"),
            containsString("temp1.add(8)"),
            containsString("temp1.add(15)"),
            containsString("AnInterface queue1 = (AnInterface) temp1;")));
        assertThat(result.getValue(), equalTo("queue1"));
    }

    @Test
    public void testTryDeserializeHiddenType() throws Exception {
        SerializedList value = new SerializedList(parameterized(PrivateList.class, null, Integer.class)).withResult(parameterized(LinkedList.class, null, Integer.class));
        value.add(literal(0));
        value.add(literal(8));
        value.add(literal(15));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), not(containsString("new net.amygdalum.testrecorder.deserializers.builder.DefaultQueueAdaptorTest.PrivateList"))); 
        assertThat(result.getStatements().toString(), allOf(
            containsString("LinkedList<Integer> queue1 = (LinkedList<Integer>) clazz(\"net.amygdalum.testrecorder.deserializers.builder.DefaultQueueAdaptorTest$PrivateList\").value();"),
            containsString("queue1.add(0)"),
            containsString("queue1.add(8)"),
            containsString("queue1.add(15)")));
        assertThat(result.getValue(), equalTo("queue1"));
    }
    
    public interface AnInterface {
        
    }
    
    private static class PrivateList<T> extends LinkedList<T> implements AnInterface {

    }
    
    public static class PublicList<T> extends LinkedList<T> implements AnInterface {

    }
    
}
