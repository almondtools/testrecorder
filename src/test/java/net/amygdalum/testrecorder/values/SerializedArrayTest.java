package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static net.amygdalum.testrecorder.util.Types.array;
import static net.amygdalum.testrecorder.util.Types.parameterized;
import static net.amygdalum.testrecorder.values.ParameterizedTypeMatcher.parameterizedType;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;

public class SerializedArrayTest {

	@SuppressWarnings("unused")
	private List<String>[] genericArray = null;

	@Test
	public void testGetResultType() throws Exception {
		SerializedArray array = new SerializedArray(String[].class);

		assertThat(array.getResultType()).isEqualTo(String[].class);
	}

	@Test
	public void testGetSetResultType() throws Exception {
		SerializedArray array = new SerializedArray(String[].class)
			.withResult(Object.class);
		
		assertThat(array.getResultType()).isEqualTo(Object.class);
	}
	
	@Test
	public void testGetComponentType() throws Exception {
		SerializedArray array = new SerializedArray(String[].class).with(literal("s1"), literal("s2"));
		assertThat(array.getComponentType()).isEqualTo(String.class);
	}

	@Test
	public void testGetComponentTypeOnGenericArray() throws Exception {
		SerializedArray array = new SerializedArray(SerializedArrayTest.class.getDeclaredField("genericArray").getGenericType());
		assertThat(array.getComponentType(), parameterizedType(List.class, String.class));
	}

	@Test
	public void testGetComponentTypeOnRuntimeGenericArray() throws Exception {
		SerializedArray array = new SerializedArray(Void.class);

		assertThat(array.getComponentType()).isEqualTo(Object.class);
	}

	@Test
	public void testGetRawType() throws Exception {
		SerializedArray array = new SerializedArray(String[].class);
		assertThat(array.getRawType()).isEqualTo(String.class);
	}

	@Test
	public void testGetRawTypeOnGenericArray() throws Exception {
		SerializedArray array = new SerializedArray(array(parameterized(List.class, null, String.class)));
		assertThat(array.getRawType()).isEqualTo(List.class);
	}

	@Test
	public void testGetArray() throws Exception {
		SerializedArray array = new SerializedArray(String[].class).with(literal("s1"), literal("s2"));

		assertThat(array.getArray()).containsExactly(literal("s1"), literal("s2"));
	}

	@Test
	public void testAccept() throws Exception {
		SerializedArray array = new SerializedArray(String[].class);

		assertThat(array.accept(new TestValueVisitor(), NULL)).isEqualTo("SerializedArray");
	}

	@Test
	public void testAdd() throws Exception {
		SerializedArray array = new SerializedArray(String[].class);
		array.add(literal("s"));

		assertThat(array.getArray()).containsExactly(literal("s"));
	}

	@Test
	public void testToString() throws Exception {
		SerializedArray array = new SerializedArray(String[].class).with(literal("s1"), literal("s2"));

		assertThat(array.toString()).isEqualTo("<s1, s2>");
	}

	@Test
	public void testReferencedValues() throws Exception {
		SerializedArray array = new SerializedArray(String[].class).with(literal("s1"), literal("s2"));
		
		assertThat(array.referencedValues()).hasSize(2);
	}

	@Test
	public void testGetArrayAsList() throws Exception {
		SerializedArray array = new SerializedArray(String[].class);
		array.add(literal("s"));

		assertThat(array.getArrayAsList()).containsExactly(literal("s"));
	}

}
