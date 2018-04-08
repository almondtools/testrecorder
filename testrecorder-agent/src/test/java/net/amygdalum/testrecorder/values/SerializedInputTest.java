package net.amygdalum.testrecorder.values;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.lang.reflect.Type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;

public class SerializedInputTest {

	private SerializedInput input;
	private SerializedInput inputNoResult;

	@BeforeEach
	public void before() throws Exception {
		input = new SerializedInput(42, BufferedReader.class, "readLine", String.class, new Type[0])
			.updateResult(literal("Hello"))
			.updateArguments(new SerializedValue[0]);
		inputNoResult = new SerializedInput(43, InputStream.class, "read", void.class, new Type[] { byte[].class, int.class, int.class })
			.updateArguments(new SerializedArray(byte.class), literal(int.class, 0), literal(int.class, 0));
	}

	@Test
	public void testGetId() throws Exception {
		assertThat(input.getId()).isEqualTo(42);
		assertThat(inputNoResult.getId()).isEqualTo(43);
	}

	@Test
	public void testGetDeclaringClass() throws Exception {
		assertThat(input.getDeclaringClass()).isSameAs(BufferedReader.class);
		assertThat(inputNoResult.getDeclaringClass()).isSameAs(InputStream.class);
	}

	@Test
	public void testGetName() throws Exception {
		assertThat(input.getName()).isEqualTo("readLine");
		assertThat(inputNoResult.getName()).isEqualTo("read");
	}

	@Test
	public void testGetTypes() throws Exception {
		assertThat(input.getTypes()).hasSize(0);
		assertThat(inputNoResult.getTypes()).containsExactly(byte[].class, int.class, int.class);
	}

	@Test
	public void testGetArguments() throws Exception {
		assertThat(input.getArguments()).hasSize(0);
		assertThat(inputNoResult.getArguments()).containsExactly(inputNoResult.getArguments()[0], literal(int.class, 0), literal(int.class, 0));
	}

	@Test
	public void testGetResultType() throws Exception {
		assertThat(input.getResultType()).isSameAs(String.class);
		assertThat(inputNoResult.getResultType()).isSameAs(void.class);
	}

	@Test
	public void testGetResult() throws Exception {
		assertThat(input.getResult()).isEqualTo(literal("Hello"));
		assertThat(inputNoResult.getResult()).isNull();
	}

	@Test
	public void testEquals() throws Exception {
		inputNoResult.equals(new SerializedInput(43, InputStream.class, "read", void.class, new Type[] { byte[].class, int.class, int.class })
			.updateArguments(new SerializedArray(byte.class), literal(int.class, 0), literal(int.class, 0)));
		assertThat(input).satisfies(defaultEquality()
			.andEqualTo(new SerializedInput(42, BufferedReader.class, "readLine", String.class, new Type[0])
				.updateResult(literal("Hello"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(inputNoResult)
			.andNotEqualTo(new SerializedInput(43, BufferedReader.class, "readLine", String.class, new Type[0])
				.updateResult(literal("Hello"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, InputStream.class, "readLine", String.class, new Type[0])
				.updateResult(literal("Hello"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, BufferedReader.class, "read", String.class, new Type[0])
				.updateResult(literal("Hello"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, BufferedReader.class, "readLine", Object.class, new Type[0])
				.updateResult(literal("Hello"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, BufferedReader.class, "readLine", String.class, new Type[0])
				.updateResult(literal("Hello World"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, BufferedReader.class, "readLine", String.class, new Type[] { int.class })
				.updateResult(literal("Hello"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, BufferedReader.class, "readLine", String.class, new Type[0])
				.updateResult(literal("Hello"))
				.updateArguments(literal("value")))
			.conventions());

		assertThat(inputNoResult).satisfies(defaultEquality()
			.andEqualTo(new SerializedInput(43, InputStream.class, "read", void.class, new Type[] { byte[].class, int.class, int.class })
				.updateArguments(inputNoResult.getArguments()))
			.andNotEqualTo(input)
			.conventions());
	}

	@Test
	public void testToString() throws Exception {
		assertThat(input.toString()).contains("BufferedReader", "readLine", "Hello");

		assertThat(inputNoResult.toString()).contains("InputStream", "void", "read", "0");
	}

}
