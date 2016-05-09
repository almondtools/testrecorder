package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedArray;

public class DefaultArrayAdaptorTest {

	private DefaultArrayAdaptor adaptor;

	@Before
	public void before() throws Exception {
		adaptor = new DefaultArrayAdaptor();
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
		SerializedArray value = new SerializedArray(int[].class);
		value.add(literal(int.class, 0));
		value.add(literal(int.class, 8));
		value.add(literal(int.class, 15));
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements().toString(), containsString("int[] intArray1 = new int[]{0, 8, 15}"));
		assertThat(result.getValue(), equalTo("intArray1"));
	}


}
