package net.amygdalum.testrecorder.values;

import static com.almondtools.conmatch.conventions.EqualityMatcher.satisfiesDefaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;

import org.junit.Before;
import org.junit.Test;

public class SerializedOutputTest {

	private SerializedOutput output;

	@Before
	public void before() throws Exception {
		output = new SerializedOutput(41, PrintStream.class, "println", new Type[] { String.class }, literal("Hello"));
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
		assertThat(output.getName(), sameInstance("println"));
	}

	@Test
	public void testGetTypes() throws Exception {
		assertThat(output.getTypes(), arrayContaining(String.class));
	}

	@Test
	public void testGetValues() throws Exception {
		assertThat(output.getValues(), arrayContaining(literal("Hello")));
	}

	@Test
	public void testEquals() throws Exception {
		assertThat(output, satisfiesDefaultEquality()
			.andEqualTo(new SerializedOutput(41, PrintStream.class, "println", new Type[] { String.class }, literal("Hello")))
			.andNotEqualTo(new SerializedOutput(42, PrintStream.class, "println", new Type[] { String.class }, literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, PrintWriter.class, "println", new Type[] { String.class }, literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, PrintStream.class, "print", new Type[] { String.class }, literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, PrintStream.class, "println", new Type[] { Object.class }, literal("Hello")))
			.andNotEqualTo(new SerializedOutput(41, PrintStream.class, "println", new Type[] { String.class }, literal("Hello World"))));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(output.toString(), containsString("PrintStream"));
		assertThat(output.toString(), containsString("println"));
		assertThat(output.toString(), containsString("Hello"));
	}

}
