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
	void before() throws Exception {
		input = new SerializedInput(42, BufferedReader.class, "readLine", String.class, new Type[0])
			.updateResult(literal("Hello"))
			.updateArguments(new SerializedValue[0]);
		inputNoResult = new SerializedInput(43, InputStream.class, "read", void.class, new Type[] { byte[].class, int.class, int.class })
			.updateArguments(new SerializedArray(byte.class), literal(int.class, 0), literal(int.class, 0))
			.updateResult(SerializedNull.VOID);
	}

	@Test
	void testGetId() throws Exception {
		assertThat(input.getId()).isEqualTo(42);
		assertThat(inputNoResult.getId()).isEqualTo(43);
	}

	@Test
	void testGetDeclaringClass() throws Exception {
		assertThat(input.getDeclaringClass()).isSameAs(BufferedReader.class);
		assertThat(inputNoResult.getDeclaringClass()).isSameAs(InputStream.class);
	}

	@Test
	void testGetName() throws Exception {
		assertThat(input.getName()).isEqualTo("readLine");
		assertThat(inputNoResult.getName()).isEqualTo("read");
	}

	@Test
	void testGetTypes() throws Exception {
		assertThat(input.getTypes()).hasSize(0);
		assertThat(inputNoResult.getTypes()).containsExactly(byte[].class, int.class, int.class);
	}

	@Test
	void testGetArguments() throws Exception {
		assertThat(input.getArguments()).hasSize(0);
		assertThat(inputNoResult.getArguments()).containsExactly(inputNoResult.getArguments()[0], literal(int.class, 0), literal(int.class, 0));
	}

	@Test
	void testGetResultType() throws Exception {
		assertThat(input.getResultType()).isSameAs(String.class);
		assertThat(inputNoResult.getResultType()).isSameAs(void.class);
	}

	@Test
	void testGetResult() throws Exception {
		assertThat(input.getResult()).isEqualTo(literal("Hello"));
		assertThat(inputNoResult.getResult()).isSameAs(SerializedNull.VOID);
	}

	@Test
	void testEquals() throws Exception {
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
				.updateArguments(inputNoResult.getArguments())
				.updateResult(SerializedNull.VOID))
			.andNotEqualTo(input)
			.conventions());
	}

	@Test
	void testToString() throws Exception {
		assertThat(input.toString()).contains("BufferedReader", "readLine", "Hello");

		assertThat(inputNoResult.toString()).contains("InputStream", "void", "read", "0");
	}

	@Test
	void testUpdateArguments() throws Exception {
		input.updateArguments((SerializedValue[]) null);
		
		assertThat(input.getArguments()).isEmpty();
	}

	@Test
	void testIsComplete() {
		assertThat(input.isComplete()).isTrue();
		assertThat(inputNoResult.isComplete()).isTrue();
	}
	
	@Test
	void testIsCompleteOnMissingResult() throws Exception {
		input.result = null;
		assertThat(input.isComplete()).isFalse();
	}
	
	@Test
	void testIsCompleteOnMissingResultType() throws Exception {
		input.resultType = null;
		assertThat(input.isComplete()).isFalse();
	}
	
	@Test
	void testIsCompleteOnMissingArgumentType() throws Exception {
		input.types = null;
		assertThat(input.isComplete()).isFalse();
	}
	
	@Test
	void testIsCompleteOnNullArguments() throws Exception {
		input.arguments = null;
		assertThat(input.isComplete()).isFalse();
	}
	
	@Test
	void testIsCompleteOnMissingArguments() throws Exception {
		input.arguments = new SerializedValue[] {null};
		assertThat(input.isComplete()).isFalse();
	}
	
	@Test
	void testHasResult() throws Exception {
		assertThat(input.hasResult()).isTrue();
		assertThat(inputNoResult.hasResult()).isFalse();
	}
	
	@Test
	void testHasResultWithNoResultType() throws Exception {
		input.resultType = null;
		assertThat(input.hasResult()).isFalse();
	}
	
	@Test
	void testHasResultWithNoResult() throws Exception {
		input.result = null;
		assertThat(input.hasResult()).isFalse();
	}
	
}
