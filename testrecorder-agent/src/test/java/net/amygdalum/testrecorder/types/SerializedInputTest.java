package net.amygdalum.testrecorder.types;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.lang.reflect.Type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.values.SerializedArray;
import net.amygdalum.testrecorder.values.SerializedLiteral;
import net.amygdalum.testrecorder.values.SerializedNull;

public class SerializedInputTest {

	private static final SerializedValue VOID = SerializedNull.VOID;
	private static final SerializedLiteral STRING_LITERAL = literal("Hello");
	private static final SerializedLiteral INT_LITERAL = literal(int.class, 0);
	private static final SerializedArray ARRAY = new SerializedArray(byte.class);

	private SerializedInput input;
	private SerializedInput inputNoResult;

	@BeforeEach
	void before() throws Exception {
		input = new SerializedInput(42, bufferedReaderReadLine())
			.updateResult(STRING_LITERAL)
			.updateArguments(new SerializedValue[0]);
		inputNoResult = new SerializedInput(43, inputStreamRead())
			.updateArguments(ARRAY, INT_LITERAL, INT_LITERAL)
			.updateResult(VOID);
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
		assertThat(input.getMethodName()).isEqualTo("readLine");
		assertThat(inputNoResult.getMethodName()).isEqualTo("read");
	}

	@Test
	void testGetTypes() throws Exception {
		assertThat(input.getArgumentTypes()).hasSize(0);
		assertThat(inputNoResult.getArgumentTypes()).containsExactly(byte[].class, int.class, int.class);
	}

	@Test
	void testGetArguments() throws Exception {
		assertThat(input.getArguments()).hasSize(0);
		assertThat(inputNoResult.getArguments()).extracting(SerializedArgument::getValue)
			.containsExactly(ARRAY, INT_LITERAL, INT_LITERAL);
	}

	@Test
	void testGetResultType() throws Exception {
		assertThat(input.getResultType()).isSameAs(String.class);
		assertThat(inputNoResult.getResultType()).isSameAs(void.class);
	}

	@Test
	void testGetResult() throws Exception {
		assertThat(input.getResult().getValue()).isEqualTo(STRING_LITERAL);
		assertThat(inputNoResult.getResult().getValue()).isSameAs(VOID);
	}

	@Test
	void testEquals() throws Exception {
		assertThat(input).satisfies(defaultEquality()
			.andEqualTo(new SerializedInput(42, bufferedReaderReadLine())
				.updateResult(STRING_LITERAL)
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(inputNoResult)
			.andNotEqualTo(new SerializedInput(43, bufferedReaderReadLine())
				.updateResult(STRING_LITERAL)
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, inputStreamRead())
				.updateResult(STRING_LITERAL)
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, bufferedReaderReadLine())
				.updateResult(literal("Hello World"))
				.updateArguments(new SerializedValue[0]))
			.conventions());

		assertThat(inputNoResult).satisfies(defaultEquality()
			.andEqualTo(new SerializedInput(43, inputStreamRead())
				.updateArguments(ARRAY, INT_LITERAL, INT_LITERAL)
				.updateResult(VOID))
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

	@Nested
	class testIsComplete {

		@Test
		void onComplete() {
			assertThat(input.isComplete()).isTrue();
			assertThat(inputNoResult.isComplete()).isTrue();
		}

		@Test
		void onMissingResult() throws Exception {
			input.result = null;
			assertThat(input.isComplete()).isFalse();
		}

		@Test
		void onNullArguments() throws Exception {
			input.arguments = null;
			assertThat(input.isComplete()).isFalse();
		}

		@Test
		void onMissingArguments() throws Exception {
			input.arguments = new SerializedArgument[] {null};
			assertThat(input.isComplete()).isFalse();
		}
	}

	@Nested
	class testHasResult {

		@Test
		void onMethod() throws Exception {
			assertThat(input.hasResult()).isTrue();
		}

		@Test
		void onVoidMethod() throws Exception {
			assertThat(inputNoResult.hasResult()).isFalse();
		}

		@Test
		void withoutResultType() throws Exception {
			input.signature.resultType = null;
			assertThat(input.hasResult()).isFalse();
		}

		@Test
		void withoutResult() throws Exception {
			input.result = null;
			assertThat(input.hasResult()).isFalse();
		}
	}

	private MethodSignature bufferedReaderReadLine() {
		return new MethodSignature(BufferedReader.class, String.class, "readLine", new Type[0]);
	}

	private MethodSignature inputStreamRead() {
		return new MethodSignature(InputStream.class, void.class, "read", new Type[] {byte[].class, int.class, int.class});
	}

}
