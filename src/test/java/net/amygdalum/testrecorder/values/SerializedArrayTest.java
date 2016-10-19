package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.util.Types.array;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.ParameterizedTypeMatcher.parameterizedType;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;

public class SerializedArrayTest {

	@SuppressWarnings("unused")
	private List<String>[] genericArray = null;

	@Test
	public void testGetResultType() throws Exception {
		SerializedArray array = new SerializedArray(String[].class).with(literal("s1"), literal("s2"));

		assertThat(array.getResultType(), equalTo(String[].class));
	}

	@Test
	public void testGetComponentType() throws Exception {
		SerializedArray array = new SerializedArray(String[].class).with(literal("s1"), literal("s2"));
		assertThat(array.getComponentType(), equalTo(String.class));
	}

	@Test
	public void testGetComponentTypeOnGenericArray() throws Exception {
		SerializedArray array = new SerializedArray(SerializedArrayTest.class.getDeclaredField("genericArray").getGenericType());
		assertThat(array.getComponentType(), parameterizedType(List.class, String.class));
	}

	@Test
	public void testGetComponentTypeOnRuntimeGenericArray() throws Exception {
		SerializedArray array = new SerializedArray(Void.class);

		assertThat(array.getComponentType(), equalTo(Object.class));
	}

	@Test
	public void testGetRawType() throws Exception {
		SerializedArray array = new SerializedArray(String[].class);
		assertThat(array.getRawType(), equalTo(String.class));
	}

	@Test
	public void testGetRawTypeOnGenericArray() throws Exception {
		SerializedArray array = new SerializedArray(array(parameterized(List.class, null, String.class)));
		assertThat(array.getRawType(), equalTo(List.class));
	}

	@Test
	public void testGetArray() throws Exception {
		SerializedArray array = new SerializedArray(String[].class).with(literal("s1"), literal("s2"));

		assertThat(array.getArray(), arrayContaining(literal("s1"), literal("s2")));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedArray array = new SerializedArray(String[].class);

		assertThat(array.accept(new TestValueVisitor()), equalTo("SerializedArray"));
	}

	@Test
	public void testAdd() throws Exception {
		SerializedArray array = new SerializedArray(String[].class);
		array.add(literal("s"));

		assertThat(array.getArray(), arrayContaining(literal("s")));
	}

	@Test
	public void testToString() throws Exception {
		SerializedArray array = new SerializedArray(String[].class).with(literal("s1"), literal("s2"));

		assertThat(array.toString(), equalTo("<s1, s2>"));
	}

	@Test
	public void testReferencedValues() throws Exception {
		SerializedArray array = new SerializedArray(String[].class).with(literal("s1"), literal("s2"));
		
		assertThat(array.referencedValues(), hasSize(2));
	}

	@Test
	public void testGetArrayAsList() throws Exception {
		SerializedArray array = new SerializedArray(String[].class);
		array.add(literal("s"));

		assertThat(array.getArrayAsList(), contains(literal("s")));
	}

}
