package net.amygdalum.testrecorder.deserializers.builder;

import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.Computation;
import net.amygdalum.testrecorder.values.SerializedList;

public class ArraysListAdaptorTest {

	private ArraysListAdaptor adaptor;

	@Before
	public void before() throws Exception {
		adaptor = new ArraysListAdaptor();
	}

	@Test
	public void testParentNull() throws Exception {
		assertThat(adaptor.parent(), sameInstance(DefaultListAdaptor.class));
	}

	@Test
	public void testMatchesDecoratorClassesInCollections() throws Exception {
		assertThat(adaptor.matches(Object.class), is(false));
		assertThat(adaptor.matches(Class.forName("java.util.Arrays$ArrayList")), is(true));
	}

	@Test
	public void testTryDeserialize() throws Exception {
		SerializedList value = listOf("java.util.Arrays$ArrayList", 0, 8, 15);
		ObjectToSetupCode generator = new ObjectToSetupCode(getClass());

		Computation result = adaptor.tryDeserialize(value, generator);

		assertThat(result.getStatements().toString(), allOf(
			containsString("Integer[] integerArray1 = new Integer[]{0, 8, 15}"),
			containsString("List<Integer> list1 = asList(integerArray1)")));
		assertThat(result.getValue(), equalTo("list1"));
	}

	private SerializedList listOf(String className, int... elements) throws ClassNotFoundException {
		SerializedList value = new SerializedList(Class.forName(className)).withResult(parameterized(List.class, null, Integer.class));
		for (int element : elements) {
			value.add(literal(element));
		}
		return value;
	}

}
