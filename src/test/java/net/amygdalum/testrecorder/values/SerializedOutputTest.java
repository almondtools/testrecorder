package net.amygdalum.testrecorder.values;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SerializedOutputTest {

	private SerializedOutput output;
	private SerializedOutput outputNoResult;

	@BeforeEach
	public void before() throws Exception {
		output = new SerializedOutput(41, PrintStream.class, "append", PrintStream.class, new Type[] { CharSequence.class })
			.updateArguments(literal("Hello"))
			.updateResult(new SerializedObject(PrintStream.class));
		outputNoResult = new SerializedOutput(41, PrintStream.class, "println", void.class, new Type[] { String.class })
			.updateArguments(literal("Hello"));
	}

	@Test
	public void testGetId() throws Exception {
		assertThat(output.getId()).isEqualTo(41);
	}

	@Test
	public void testGetDeclaringClass() throws Exception {
		assertThat(output.getDeclaringClass()).isSameAs(PrintStream.class);
	}

	@Test
	public void testGetName() throws Exception {
		assertThat(output.getName()).isSameAs("append");
		assertThat(outputNoResult.getName()).isSameAs("println");
	}

	@Test
	public void testGetTypes() throws Exception {
		assertThat(output.getTypes()).containsExactly(CharSequence.class);
		assertThat(outputNoResult.getTypes()).containsExactly(String.class);
	}

	@Test
	public void testGetArguments() throws Exception {
		assertThat(output.getArguments()).containsExactly(literal("Hello"));
	}

	@Test
	public void testGetResultType() throws Exception {
		assertThat(output.getResultType()).isSameAs(PrintStream.class);
		assertThat(outputNoResult.getResultType()).isSameAs(void.class);
	}

	@Test
	public void testGetResult() throws Exception {
		assertThat(output.getResult()).isInstanceOf(SerializedObject.class);
		assertThat(outputNoResult.getResult()).isNull();
	}

	@Test
	public void testEquals() throws Exception {
		assertThat(outputNoResult).satisfies(defaultEquality()
			.andEqualTo(new SerializedOutput(41, PrintStream.class, "println", void.class, new Type[] { String.class })
				.updateArguments(literal("Hello")))
			.andNotEqualTo(output)
			.andNotEqualTo(new SerializedOutput(42, PrintStream.class, "println", void.class, new Type[] { String.class })
				.updateArguments(literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, PrintWriter.class, "println", void.class, new Type[] { String.class })
				.updateArguments(literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, PrintStream.class, "print", void.class, new Type[] { String.class })
				.updateArguments(literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, PrintStream.class, "println", void.class, new Type[] { Object.class })
				.updateArguments(literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, PrintStream.class, "println", void.class, new Type[] { String.class })
				.updateArguments(literal("Hello World")))
			.conventions());

		assertThat(output).satisfies(defaultEquality()
			.andEqualTo(new SerializedOutput(41, PrintStream.class, "append", PrintStream.class, new Type[] { CharSequence.class })
				.updateArguments(literal("Hello"))
				.updateResult(output.getResult()))
			.andNotEqualTo(outputNoResult)
			.andNotEqualTo(new SerializedOutput(41, PrintStream.class, "append", PrintStream.class, new Type[] { CharSequence.class })
				.updateArguments(literal("Hello"))
				.updateResult(null))
			.andNotEqualTo(new SerializedOutput(41, PrintStream.class, "append", OutputStream.class, new Type[] { CharSequence.class })
				.updateArguments(literal("Hello"))
				.updateResult(output.getResult()))
			.conventions());
	}

	@Test
	public void testToString() throws Exception {
		assertThat(output.toString()).contains("PrintStream", "append", "Hello");

		assertThat(outputNoResult.toString()).contains("PrintStream", "println", "Hello");
	}

}
