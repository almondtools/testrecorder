package net.amygdalum.testrecorder.values;

import static com.almondtools.conmatch.conventions.EqualityMatcher.satisfiesDefaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;

import org.junit.Before;
import org.junit.Test;

public class SerializedOutputTest {

	private StackTraceElement caller;
	private StackTraceElement notcaller;
	private SerializedOutput output;
	private SerializedOutput outputNoResult;

	@Before
	public void before() throws Exception {
		caller = new StackTraceElement("class", "method", "file", 4711);
		notcaller = new StackTraceElement("class", "method", "file", 815);

		output = new SerializedOutput(41, caller, PrintStream.class, "append", PrintStream.class, new SerializedObject(PrintStream.class), new Type[] { CharSequence.class }, literal("Hello"));
		outputNoResult = new SerializedOutput(41, caller, PrintStream.class, "println", new Type[] { String.class }, literal("Hello"));
	}

	@Test
	public void testGetId() throws Exception {
		assertThat(output.getId(), equalTo(41));
	}

	@Test
	public void testGetDeclaringClass() throws Exception {
		assertThat(output.getDeclaringClass(), sameInstance(PrintStream.class));
	}

	@Test
	public void testGetName() throws Exception {
		assertThat(output.getName(), sameInstance("append"));
		assertThat(outputNoResult.getName(), sameInstance("println"));
	}

	@Test
	public void testGetTypes() throws Exception {
		assertThat(output.getTypes(), arrayContaining(CharSequence.class));
		assertThat(outputNoResult.getTypes(), arrayContaining(String.class));
	}

	@Test
		public void testGetArguments() throws Exception {
			assertThat(output.getArguments(), arrayContaining(literal("Hello")));
		}

	@Test
	public void testGetResultType() throws Exception {
		assertThat(output.getResultType(), sameInstance(PrintStream.class));
		assertThat(outputNoResult.getResultType(), sameInstance(void.class));
	}

	@Test
	public void testGetResult() throws Exception {
		assertThat(output.getResult(), instanceOf(SerializedObject.class));
		assertThat(outputNoResult.getResult(), nullValue());
	}

	@Test
	public void testEquals() throws Exception {
		assertThat(outputNoResult, satisfiesDefaultEquality()
			.andEqualTo(new SerializedOutput(41, caller, PrintStream.class, "println", new Type[] { String.class }, literal("Hello")))
			.andNotEqualTo(output)
			.andNotEqualTo(new SerializedOutput(41, notcaller, PrintStream.class, "println", new Type[] { String.class }, literal("Hello")))
			.andNotEqualTo(new SerializedOutput(42, caller, PrintStream.class, "println", new Type[] { String.class }, literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, caller, PrintWriter.class, "println", new Type[] { String.class }, literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, caller, PrintStream.class, "print", new Type[] { String.class }, literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, caller, PrintStream.class, "println", new Type[] { Object.class }, literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, caller, PrintStream.class, "println", new Type[] { String.class }, literal("Hello World"))));

		assertThat(output, satisfiesDefaultEquality()
			.andEqualTo(new SerializedOutput(41, caller, PrintStream.class, "append", PrintStream.class, output.getResult(), new Type[] { CharSequence.class }, literal("Hello")))
			.andNotEqualTo(outputNoResult)
			.andNotEqualTo(new SerializedOutput(41, notcaller, PrintStream.class, "append", PrintStream.class, output.getResult(), new Type[] { CharSequence.class }, literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, caller, PrintStream.class, "append", PrintStream.class, null, new Type[] { CharSequence.class }, literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, caller, PrintStream.class, "append", OutputStream.class, output.getResult(), new Type[] { CharSequence.class }, literal("Hello"))));
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

}
