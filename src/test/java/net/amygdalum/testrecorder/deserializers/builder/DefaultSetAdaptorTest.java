package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.util.testobjects.Hidden.classOfHiddenSet;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.deserializers.LocalVariable;
import net.amygdalum.testrecorder.deserializers.LocalVariableDefinition;
import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.testobjects.OrthogonalInterface;
import net.amygdalum.testrecorder.util.testobjects.PublicSet;
import net.amygdalum.testrecorder.values.SerializedSet;

public class DefaultSetAdaptorTest {

	private DefaultSetAdaptor adaptor;

	@BeforeEach
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
		
		Computation result = adaptor.tryDeserialize(value, generator, NULL);
		
		assertThat(result.getStatements().toString(), allOf(
			containsString("LinkedHashSet<Integer> temp1 = new LinkedHashSet<Integer>()"),
			containsString("temp1.add(0)"),
			containsString("temp1.add(8)"),
			containsString("temp1.add(15)"),
			containsString("Set<Integer> set1 = temp1;")));
		assertThat(result.getValue()).isEqualTo("set1");
	}

	@Test
	public void testTryDeserializeSameResultTypes() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(LinkedHashSet.class, null, Integer.class)).withResult(parameterized(LinkedHashSet.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		SetupGenerators generator = new SetupGenerators(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator, NULL);
		
		assertThat(result.getStatements().toString(), allOf(
			containsString("LinkedHashSet<Integer> set1 = new LinkedHashSet<Integer>()"),
			containsString("set1.add(0)"),
			containsString("set1.add(8)"),
			containsString("set1.add(15)")));
		assertThat(result.getValue()).isEqualTo("set1");
	}

    @Test
    public void testTryDeserializeNonListResult() throws Exception {
        SerializedSet value = new SerializedSet(parameterized(PublicSet.class, null, Integer.class))
            .withResult(OrthogonalInterface.class);
        value.add(literal(0));
        value.add(literal(8));
        value.add(literal(15));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator, NULL);

        assertThat(result.getStatements().toString(), allOf(
            containsString("PublicSet<Integer> temp1 = new PublicSet<Integer>()"), 
            containsString("temp1.add(0)"),
            containsString("temp1.add(8)"),
            containsString("temp1.add(15)"),
            containsString("OrthogonalInterface set1 = temp1;")));
        assertThat(result.getValue()).isEqualTo("set1");
    }

    @Test
    public void testTryDeserializeNeedingAdaptation() throws Exception {
        SerializedSet value = new SerializedSet(parameterized(classOfHiddenSet(), null, Integer.class))
            .withResult(OrthogonalInterface.class);
        value.add(literal(0));
        value.add(literal(8));
        value.add(literal(15));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator, NULL);

        assertThat(result.getStatements().toString(), allOf(
            containsString("java.util.Set temp1 = (java.util.Set<?>) clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenSet\").value();"),
            containsString("temp1.add(0)"),
            containsString("temp1.add(8)"),
            containsString("temp1.add(15)"),
            containsString("OrthogonalInterface set1 = (OrthogonalInterface) temp1;")));
        assertThat(result.getValue()).isEqualTo("set1");
    }

    @Test
    public void testTryDeserializeNeedingHiddenAdaptation() throws Exception {
        SerializedSet value = new SerializedSet(parameterized(classOfHiddenSet(), null, Integer.class)).withResult(parameterized(LinkedHashSet.class, null, Integer.class));
        value.add(literal(0));
        value.add(literal(8));
        value.add(literal(15));
        SetupGenerators generator = new SetupGenerators(Object.class);

        Computation result = adaptor.tryDeserialize(value, generator, NULL);

        assertThat(result.getStatements().toString(), not(containsString("new net.amygdalum.testrecorder.util.testobjects.Hidden.HiddenSet"))); 
        assertThat(result.getStatements().toString(), allOf(
            containsString("LinkedHashSet<Integer> set1 = (LinkedHashSet<Integer>) clazz(\"net.amygdalum.testrecorder.util.testobjects.Hidden$HiddenSet\").value();"), 
            containsString("set1.add(0)"),
            containsString("set1.add(8)"),
            containsString("set1.add(15)")));
        assertThat(result.getValue()).isEqualTo("set1");
    }

    @Test
    public void testTryDeserializeForwarded() throws Exception {
        SerializedSet value = new SerializedSet(parameterized(LinkedHashSet.class, null, Integer.class)).withResult(parameterized(Set.class, null, Integer.class));
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

        Computation result = adaptor.tryDeserialize(value, generator, NULL);

        assertThat(result.getStatements().toString(), allOf(
            containsString("LinkedHashSet<Integer> temp1 = new LinkedHashSet<Integer>()"),
            containsString("temp1.add(0)"),
            containsString("temp1.add(8)"),
            containsString("temp1.add(15)"),
            containsString("forwarded.addAll(temp1);")));
        assertThat(result.getValue()).isEqualTo("forwarded");
    }

}
