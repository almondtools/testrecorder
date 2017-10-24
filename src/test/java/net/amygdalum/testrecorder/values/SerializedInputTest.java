package net.amygdalum.testrecorder.values;

import static com.almondtools.conmatch.conventions.EqualityMatcher.satisfiesDefaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.lang.reflect.Type;

import org.junit.Before;
import org.junit.Test;

import net.amygdalum.testrecorder.SerializedValue;

public class SerializedInputTest {

	private SerializedInput input;
	private SerializedInput inputNoResult;

	@Before
	public void before() throws Exception {
		input = new SerializedInput(42, "caller", BufferedReader.class, "readLine", String.class, literal("Hello"), new Type[0], new SerializedValue[0]);
		inputNoResult = new SerializedInput(43, "caller", InputStream.class, "read", new Type[] { byte[].class, int.class, int.class }, new SerializedArray(byte.class), literal(int.class, 0),
			literal(int.class, 0));
	}

	@Test
	public void testGetId() throws Exception {
		assertThat(input.getId(), equalTo(42));
		assertThat(inputNoResult.getId(), equalTo(43));
	}

	@Test
	public void testGetDeclaringClass() throws Exception {
		assertThat(input.getDeclaringClass(), sameInstance(BufferedReader.class));
		assertThat(inputNoResult.getDeclaringClass(), sameInstance(InputStream.class));
	}

	@Test
	public void testGetName() throws Exception {
		assertThat(input.getName(), equalTo("readLine"));
		assertThat(inputNoResult.getName(), equalTo("read"));
	}

	@Test
	public void testGetTypes() throws Exception {
		assertThat(input.getTypes(), arrayWithSize(0));
		assertThat(inputNoResult.getTypes(), arrayContaining(byte[].class, int.class, int.class));
	}

	@Test
	public void testGetValues() throws Exception {
		assertThat(input.getValues(), arrayWithSize(0));
		assertThat(inputNoResult.getValues(), arrayContaining(inputNoResult.getValues()[0], literal(int.class, 0), literal(int.class, 0)));
	}

	@Test
	public void testGetResultType() throws Exception {
		assertThat(input.getResultType(), sameInstance(String.class));
		assertThat(inputNoResult.getResultType(), sameInstance(void.class));
	}

	@Test
	public void testGetResult() throws Exception {
		assertThat(input.getResult(), equalTo(literal("Hello")));
		assertThat(inputNoResult.getResult(), nullValue());
	}

	@Test
	public void testEquals() throws Exception {
		inputNoResult.equals(
			new SerializedInput(43, "caller", InputStream.class, "read", new Type[] { byte[].class, int.class, int.class }, new SerializedArray(byte.class), literal(int.class, 0),
				literal(int.class, 0)));
		assertThat(input, satisfiesDefaultEquality()
			.andEqualTo(new SerializedInput(42, "caller", BufferedReader.class, "readLine", String.class, literal("Hello"), new Type[0], new SerializedValue[0]))
			.andNotEqualTo(inputNoResult)
			.andNotEqualTo(new SerializedInput(42, "notcaller", BufferedReader.class, "readLine", String.class, literal("Hello"), new Type[0], new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(43, "caller", BufferedReader.class, "readLine", String.class, literal("Hello"), new Type[0], new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, "caller", InputStream.class, "readLine", String.class, literal("Hello"), new Type[0], new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, "caller", BufferedReader.class, "read", String.class, literal("Hello"), new Type[0], new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, "caller", BufferedReader.class, "readLine", Object.class, literal("Hello"), new Type[0], new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, "caller", BufferedReader.class, "readLine", String.class, literal("Hello World"), new Type[0], new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, "caller", BufferedReader.class, "readLine", String.class, literal("Hello"), new Type[] { int.class }, new SerializedValue[0]))
			.andNotEqualTo(new SerializedInput(42, "caller", BufferedReader.class, "readLine", String.class, literal("Hello"), new Type[0], literal("value"))));

		assertThat(inputNoResult, satisfiesDefaultEquality()
			.andEqualTo(new SerializedInput(43, "caller", InputStream.class, "read", new Type[] { byte[].class, int.class, int.class }, inputNoResult.getValues()))
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

}
