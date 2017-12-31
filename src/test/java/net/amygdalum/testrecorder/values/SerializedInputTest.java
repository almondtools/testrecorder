package net.amygdalum.testrecorder.values;

import static com.almondtools.conmatch.conventions.EqualityMatcher.satisfiesDefaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.lang.reflect.Type;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;

public class SerializedInputTest {

	private StackTraceElement caller;
	private StackTraceElement notcaller;
	private SerializedInput input;
	private SerializedInput inputNoResult;

	@BeforeEach
	public void before() throws Exception {
		caller = new StackTraceElement("class", "method", "file", 4711);
		notcaller = new StackTraceElement("class", "method", "file", 815);
		
		input = new SerializedInput(42, call(caller, BufferedReader.class, "readLine"), BufferedReader.class, "readLine", String.class, new Type[0])
			.updateResult(literal("Hello"))
			.updateArguments(new SerializedValue[0]);
		inputNoResult = new SerializedInput(43, call(caller, InputStream.class, "read"), InputStream.class, "read", void.class, new Type[] { byte[].class, int.class, int.class })
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
		assertThat(input.getTypes(), arrayWithSize(0));
		assertThat(inputNoResult.getTypes()).containsExactly(byte[].class, int.class, int.class);
	}

	@Test
		public void testGetArguments() throws Exception {
			assertThat(input.getArguments(), arrayWithSize(0));
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
		assertThat(inputNoResult.getResult(), nullValue());
	}

	@Test
	public void testEquals() throws Exception {
		inputNoResult.equals(new SerializedInput(43, call(caller, InputStream.class, "read"), InputStream.class, "read", void.class, new Type[] { byte[].class, int.class, int.class })
			.updateArguments(new SerializedArray(byte.class), literal(int.class, 0), literal(int.class, 0)));
		assertThat(input, satisfiesDefaultEquality()
			.andEqualTo(new SerializedInput(42, call(caller, BufferedReader.class, "readLine"), BufferedReader.class, "readLine", String.class, new Type[0])
				.updateResult(literal("Hello"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(inputNoResult)
			.andNotEqualTo(new SerializedInput(42, call(notcaller, BufferedReader.class, "readLine"), BufferedReader.class, "readLine", String.class, new Type[0])
				.updateResult(literal("Hello"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(43, call(caller, BufferedReader.class, "readLine"), BufferedReader.class, "readLine", String.class, new Type[0])
				.updateResult(literal("Hello"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, call(caller, InputStream.class, "readLine"), InputStream.class, "readLine", String.class, new Type[0])
				.updateResult(literal("Hello"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, call(caller, BufferedReader.class, "read"), BufferedReader.class, "read", String.class, new Type[0])
				.updateResult(literal("Hello"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, call(caller, BufferedReader.class, "readLine"), BufferedReader.class, "readLine", Object.class, new Type[0])
				.updateResult(literal("Hello"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, call(caller, BufferedReader.class, "readLine"), BufferedReader.class, "readLine", String.class, new Type[0])
				.updateResult(literal("Hello World"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, call(caller, BufferedReader.class, "readLine"), BufferedReader.class, "readLine", String.class, new Type[] { int.class })
				.updateResult(literal("Hello"))
				.updateArguments(new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, call(caller, BufferedReader.class, "readLine"), BufferedReader.class, "readLine", String.class, new Type[0])
				.updateResult(literal("Hello"))
				.updateArguments(literal("value"))));

		assertThat(inputNoResult, satisfiesDefaultEquality()
			.andEqualTo(new SerializedInput(43, call(caller, InputStream.class, "read"), InputStream.class, "read", void.class, new Type[] { byte[].class, int.class, int.class })
				.updateArguments(inputNoResult.getArguments()))
			.andNotEqualTo(input));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(input.toString(), containsString("BufferedReader"));
		assertThat(input.toString(), containsString("readLine"));
		assertThat(input.toString(), containsString("Hello"));

		assertThat(inputNoResult.toString(), containsString("InputStream"));
		assertThat(inputNoResult.toString(), containsString("void"));
		assertThat(inputNoResult.toString(), containsString("read"));
		assertThat(inputNoResult.toString(), containsString("0"));
	}

	private StackTraceElement[] call(StackTraceElement caller, Class<?> clazz, String method) {
		StackTraceElement callee = new StackTraceElement(clazz.getName(), method, "", 0);
		return new StackTraceElement[] {caller, callee};
	}

}
