package net.amygdalum.testrecorder.values;

import static com.almondtools.conmatch.conventions.EqualityMatcher.satisfiesDefaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SerializedOutputTest {

	private StackTraceElement caller;
	private StackTraceElement notcaller;
	private SerializedOutput output;
	private SerializedOutput outputNoResult;

	@BeforeEach
	public void before() throws Exception {
		caller = new StackTraceElement("class", "method", "file", 4711);
		notcaller = new StackTraceElement("class", "method", "file", 815);

		output = new SerializedOutput(41, call(caller, PrintStream.class, "append"), PrintStream.class, "append", PrintStream.class, new Type[] { CharSequence.class })
			.updateArguments(literal("Hello"))
			.updateResult(new SerializedObject(PrintStream.class));
		outputNoResult = new SerializedOutput(41, call(caller, PrintStream.class, "println"), PrintStream.class, "println", void.class, new Type[] { String.class })
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
		assertThat(output.getResult(), instanceOf(SerializedObject.class));
		assertThat(outputNoResult.getResult(), nullValue());
	}

	@Test
	public void testEquals() throws Exception {
		assertThat(outputNoResult, satisfiesDefaultEquality()
			.andEqualTo(new SerializedOutput(41, call(caller, PrintStream.class, "println"), PrintStream.class, "println", void.class, new Type[] { String.class })
				.updateArguments(literal("Hello")))
			.andNotEqualTo(output)
			.andNotEqualTo(new SerializedOutput(41, call(notcaller, PrintStream.class, "println"), PrintStream.class, "println", void.class, new Type[] { String.class })
				.updateArguments(literal("Hello")))
			.andNotEqualTo(new SerializedOutput(42, call(caller, PrintStream.class, "println"), PrintStream.class, "println", void.class, new Type[] { String.class })
				.updateArguments(literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, call(caller, PrintWriter.class, "println"), PrintWriter.class, "println", void.class, new Type[] { String.class })
				.updateArguments(literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, call(caller, PrintStream.class, "print"), PrintStream.class, "print", void.class, new Type[] { String.class })
				.updateArguments(literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, call(caller, PrintStream.class, "println"), PrintStream.class, "println", void.class, new Type[] { Object.class })
				.updateArguments(literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, call(caller, PrintStream.class, "println"), PrintStream.class, "println", void.class, new Type[] { String.class })
				.updateArguments(literal("Hello World"))));

		assertThat(output, satisfiesDefaultEquality()
			.andEqualTo(new SerializedOutput(41, call(caller, PrintStream.class, "append"), PrintStream.class, "append", PrintStream.class, new Type[] { CharSequence.class })
				.updateArguments(literal("Hello"))
				.updateResult(output.getResult()))
			.andNotEqualTo(outputNoResult)
			.andNotEqualTo(new SerializedOutput(41, call(notcaller, PrintStream.class, "append"), PrintStream.class, "append", PrintStream.class, new Type[] { CharSequence.class })
				.updateArguments(literal("Hello"))
				.updateResult(output.getResult()))
			.andNotEqualTo(new SerializedOutput(41, call(caller, PrintStream.class, "append"), PrintStream.class, "append", PrintStream.class, new Type[] { CharSequence.class })
				.updateArguments(literal("Hello"))
				.updateResult(null))
			.andNotEqualTo(new SerializedOutput(41, call(caller, PrintStream.class, "append"), PrintStream.class, "append", OutputStream.class, new Type[] { CharSequence.class })
				.updateArguments(literal("Hello"))
				.updateResult(output.getResult())));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(output.toString(), containsString("PrintStream"));
		assertThat(output.toString(), containsString("append"));
		assertThat(output.toString(), containsString("Hello"));

		assertThat(outputNoResult.toString(), containsString("PrintStream"));
		assertThat(outputNoResult.toString(), containsString("println"));
		assertThat(outputNoResult.toString(), containsString("Hello"));
	}

	private StackTraceElement[] call(StackTraceElement caller, Class<?> clazz, String method) {
		StackTraceElement callee = new StackTraceElement(clazz.getName(), method, "", 0);
		return new StackTraceElement[] { caller, callee };
	}

}
