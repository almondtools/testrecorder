package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedList;

public class DefaultListAdaptorTest {

	private DefaultListAdaptor adaptor;

	@Before
	public void before() throws Exception {
		adaptor = new DefaultListAdaptor();
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
		SerializedList value = new SerializedList(parameterized(ArrayList.class, null, Integer.class)).withResult(parameterized(List.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements().toString(), allOf(
			containsString("ArrayList<Integer> temp1 = new ArrayList<Integer>()"),
			containsString("temp1.add(0)"),
			containsString("temp1.add(8)"),
			containsString("temp1.add(15)"),
			containsString("List<Integer> list1 = temp1;")));
		assertThat(result.getValue(), equalTo("list1"));
	}
	
	@Test
	public void testTryDeserializeSameResultTypes() throws Exception {
		SerializedList value = new SerializedList(parameterized(ArrayList.class, null, Integer.class)).withResult(parameterized(ArrayList.class, null, Integer.class));
		value.add(literal(0));
		value.add(literal(8));
		value.add(literal(15));
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements().toString(), allOf(
			containsString("ArrayList<Integer> list1 = new ArrayList<Integer>()"),
			containsString("list1.add(0)"),
			containsString("list1.add(8)"),
			containsString("list1.add(15)")));
		assertThat(result.getValue(), equalTo("list1"));
	}
	
}
