package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
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
		assertThat(adaptor.matches(Object.class),is(true));
		assertThat(adaptor.matches(new Object(){}.getClass()),is(true));
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedMap value = new SerializedMap(parameterized(LinkedHashMap.class, null, Integer.class, Integer.class)).withResult(parameterized(Map.class, null, Integer.class, Integer.class));
		value.put(literal(8), literal(15));
		value.put(literal(47), literal(11));
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());
		
		Computation result = adaptor.tryDeserialize(value, generator);
		
		assertThat(result.getStatements().toString(), allOf(
			containsString("Map<Integer, Integer> map1 = new LinkedHashMap<Integer, Integer>()"),
			containsString("map1.put(8, 15)"),
			containsString("map1.put(47, 11);")));
		assertThat(result.getValue(), equalTo("map1"));
	}


}
