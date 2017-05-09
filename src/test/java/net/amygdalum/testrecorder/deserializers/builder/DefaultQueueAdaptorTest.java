package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.classOfHiddenQueue;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.SerializedValue;
import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.LocalVariable;
import net.amygdalum.testrecorder.deserializers.LocalVariableDefinition;
import net.amygdalum.testrecorder.util.testobjects.OrthogonalInterface;
import net.amygdalum.testrecorder.util.testobjects.PublicQueue;
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
        SerializedList value = new SerializedList(parameterized(PublicQueue.class, null, Integer.class))
            .withResult(OrthogonalInterface.class);
        value.add(literal(0));
        value.add(literal(8));
        value.add(literal(15));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), allOf(
            containsString("PublicQueue<Integer> temp1 = new PublicQueue<Integer>()"), 
            containsString("temp1.add(0)"),
            containsString("temp1.add(8)"),
            containsString("temp1.add(15)"),
            containsString("OrthogonalInterface queue1 = temp1;")));
        assertThat(result.getValue(), equalTo("queue1"));
    }

    @Test
    public void testTryDeserializeNeedingAdaptation() throws Exception {
        SerializedList value = new SerializedList(parameterized(classOfHiddenQueue(), null, Integer.class))
            .withResult(OrthogonalInterface.class);
        value.add(literal(0));
        value.add(literal(8));
        value.add(literal(15));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), allOf(
            containsString("java.util.Queue temp1 = (java.util.Queue<?>) clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenQueue\").value();"),
            containsString("temp1.add(0)"),
            containsString("temp1.add(8)"),
            containsString("temp1.add(15)"),
            containsString("OrthogonalInterface queue1 = (OrthogonalInterface) temp1;")));
        assertThat(result.getValue(), equalTo("queue1"));
    }

    @Test
    public void testTryDeserializeHiddenType() throws Exception {
        SerializedList value = new SerializedList(parameterized(classOfHiddenQueue(), null, Integer.class)).withResult(parameterized(LinkedList.class, null, Integer.class));
        value.add(literal(0));
        value.add(literal(8));
        value.add(literal(15));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), not(containsString("new net.amygdalum.testrecorder.util.testobjects.Hidden.HiddenQueue"))); 
        assertThat(result.getStatements().toString(), allOf(
            containsString("LinkedList<Integer> queue1 = (LinkedList<Integer>) clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenQueue\").value();"),
            containsString("queue1.add(0)"),
            containsString("queue1.add(8)"),
            containsString("queue1.add(15)")));
        assertThat(result.getValue(), equalTo("queue1"));
    }
    
    @Test
    public void testTryDeserializeForwarded() throws Exception {
        SerializedList value = new SerializedList(parameterized(LinkedList.class, null, Integer.class)).withResult(parameterized(List.class, null, Integer.class));
        value.add(literal(0));
        value.add(literal(8));
        value.add(literal(15));
        SetupGenerators generator = new SetupGenerators(getClass()) {
            @Override
            public Computation forVariable(SerializedValue value, Type type, LocalVariableDefinition computation) {
                LocalVariable local = new LocalVariable("forwarded");
                local.define(type);
                return computation.define(local);
            }
        };

        Computation result = adaptor.tryDeserialize(value, generator);

        assertThat(result.getStatements().toString(), allOf(
            containsString("LinkedList<Integer> temp1 = new LinkedList<Integer>()"),
            containsString("temp1.add(0)"),
            containsString("temp1.add(8)"),
            containsString("temp1.add(15)"),
            containsString("forwarded.addAll(temp1);")));
        assertThat(result.getValue(), equalTo("forwarded"));
    }

}
