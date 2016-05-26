package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.LinkedHashSet;
import java.util.Set;

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
	public void testMatchesAnyArray() throws Exception {
		assertThat(adaptor.matches(Object.class),is(true));
		assertThat(adaptor.matches(new Object(){}.getClass()),is(true));
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedSet value = new SerializedSet(parameterized(LinkedHashSet.class, null, Integer.class)).withResult(parameterized(Set.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());
		
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
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements().toString(), allOf(
			containsString("LinkedHashSet<Integer> set1 = new LinkedHashSet<Integer>()"),
			containsString("set1.add(0)"),
			containsString("set1.add(8)"),
			containsString("set1.add(15)")));
		assertThat(result.getValue(), equalTo("set1"));
	}

}
