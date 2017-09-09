package net.amygdalum.testrecorder.deserializers;

import static com.almondtools.conmatch.conventions.UtilityClassMatcher.isUtilityClass;
import static net.amygdalum.testrecorder.deserializers.Templates.asLiteral;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Test;

public class TemplatesTest {

	@Test
	public void testTemplates() throws Exception {
		assertThat(Templates.class, isUtilityClass());
	}

	@Test
	public void testAsLiteralCharacter() throws Exception {
		assertThat(asLiteral('\n'), equalTo("'\\n'"));
		assertThat(asLiteral('\r'), equalTo("'\\r'"));
		assertThat(asLiteral('\''), equalTo("'\\''"));
		assertThat(asLiteral('\\'), equalTo("'\\\\'"));
		assertThat(asLiteral((char) 0x9), equalTo("'\\u0009'"));
		assertThat(asLiteral((char) 0x19), equalTo("'\\u0019'"));
		assertThat(asLiteral((char) 0x7f), equalTo("'\\u007f'"));
		assertThat(asLiteral((char) 0x100), equalTo("'\\u0100'"));
		assertThat(asLiteral((char) 0x1000), equalTo("'\\u1000'"));
		assertThat(asLiteral(' '), equalTo("' '"));
		assertThat(asLiteral('~'), equalTo("'~'"));
	}

	@Test
	public void testAsLiteralString() throws Exception {
		assertThat(asLiteral("\n"), equalTo("\"\\n\""));
		assertThat(asLiteral("\r"), equalTo("\"\\r\""));
		assertThat(asLiteral("\'"), equalTo("\"'\""));
		assertThat(asLiteral("\""), equalTo("\"\\\"\""));
		assertThat(asLiteral("\\"), equalTo("\"\\\\\""));
		assertThat(asLiteral(String.valueOf((char) 0x9)), equalTo("\"\\u0009\""));
		assertThat(asLiteral(String.valueOf((char) 0x19)), equalTo("\"\\u0019\""));
		assertThat(asLiteral(String.valueOf((char) 0x7f)), equalTo("\"\\u007f\""));
		assertThat(asLiteral(String.valueOf((char) 0x100)), equalTo("\"\\u0100\""));
		assertThat(asLiteral(String.valueOf((char) 0x1000)), equalTo("\"\\u1000\""));
		assertThat(asLiteral(" "), equalTo("\" \""));
		assertThat(asLiteral("~"), equalTo("\"~\""));
		assertThat(asLiteral("a-zA-Z0-9"), equalTo("\"a-zA-Z0-9\""));
		assertThat(asLiteral((String) null), equalTo("null"));
	}

	@Test
	public void testAsLiteralFloat() throws Exception {
		assertThat(asLiteral(Float.POSITIVE_INFINITY), equalTo("Float.POSITIVE_INFINITY"));
		assertThat(asLiteral(Float.NEGATIVE_INFINITY), equalTo("Float.NEGATIVE_INFINITY"));
		assertThat(asLiteral(Float.NaN), equalTo("Float.NaN"));
		assertThat(asLiteral(0f), equalTo("0.0f"));
		assertThat(asLiteral(Float.MIN_VALUE), equalTo("1.4E-45f"));
		assertThat(asLiteral(Float.MAX_VALUE), equalTo("3.4028235E38f"));
	}

	@Test
	public void testAsLiteralDouble() throws Exception {
		assertThat(asLiteral(Double.POSITIVE_INFINITY), equalTo("Double.POSITIVE_INFINITY"));
		assertThat(asLiteral(Double.NEGATIVE_INFINITY), equalTo("Double.NEGATIVE_INFINITY"));
		assertThat(asLiteral(Double.NaN), equalTo("Double.NaN"));
		assertThat(asLiteral(0d), equalTo("0.0"));
		assertThat(asLiteral(Double.MIN_VALUE), equalTo("4.9E-324"));
		assertThat(asLiteral(Double.MAX_VALUE), equalTo("1.7976931348623157E308"));
	}

	@Test
	public void testAsLiteralOther() throws Exception {
		assertThat(asLiteral((Object) "String"), equalTo("\"String\""));
		assertThat(asLiteral((Object) 'c'), equalTo("'c'"));
		assertThat(asLiteral((byte) 42), equalTo("(byte) 42"));
		assertThat(asLiteral((short) 43), equalTo("(short) 43"));
		assertThat(asLiteral((int) 44), equalTo("44"));
		assertThat(asLiteral((long) 45), equalTo("45l"));
		assertThat(asLiteral((Object) Float.NaN), equalTo("Float.NaN"));
		assertThat(asLiteral((Object) Double.NaN), equalTo("Double.NaN"));
		assertThat(asLiteral((Object) null), equalTo("null"));
		assertThat(asLiteral(new ArrayList<>()), equalTo("[]"));
	}
}
