package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.deserializers.TypeManager.parameterized;
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
		assertThat(adaptor.matches(int[].class),is(true));
		assertThat(adaptor.matches(Object[].class),is(true));
		assertThat(adaptor.matches(Integer[].class),is(true));
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedList value = new SerializedList(parameterized(List.class, null, Integer.class), ArrayList.class);
		value.add(literal(Integer.class, 0));
		value.add(literal(Integer.class, 8));
		value.add(literal(Integer.class, 15));
		ObjectToSetupCode generator = new ObjectToSetupCode();
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements().toString(), allOf(
			containsString("List<Integer> list1 = new ArrayList<>()"),
			containsString("list1.add(0)"),
			containsString("list1.add(8)"),
			containsString("list1.add(15);")));
		assertThat(result.getValue(), equalTo("list1"));
	}


}
