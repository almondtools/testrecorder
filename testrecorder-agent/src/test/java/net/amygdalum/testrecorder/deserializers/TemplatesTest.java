package net.amygdalum.testrecorder.deserializers;

import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static net.amygdalum.testrecorder.util.Literals.asLiteral;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TemplatesTest {

	@Test
	public void testTemplates() throws Exception {
		assertThat(Templates.class).satisfies(utilityClass().conventions());
	}

	@Test
	public void testAsLiteralCharacter() throws Exception {
		assertThat(asLiteral('\n')).isEqualTo("'\\n'");
		assertThat(asLiteral('\r')).isEqualTo("'\\r'");
		assertThat(asLiteral('\'')).isEqualTo("'\\''");
		assertThat(asLiteral('\\')).isEqualTo("'\\\\'");
		assertThat(asLiteral((char) 0x9)).isEqualTo("'\\u0009'");
		assertThat(asLiteral((char) 0x19)).isEqualTo("'\\u0019'");
		assertThat(asLiteral((char) 0x7f)).isEqualTo("'\\u007f'");
		assertThat(asLiteral((char) 0x100)).isEqualTo("'\\u0100'");
		assertThat(asLiteral((char) 0x1000)).isEqualTo("'\\u1000'");
		assertThat(asLiteral(' ')).isEqualTo("' '");
		assertThat(asLiteral('~')).isEqualTo("'~'");
	}

	@Test
	public void testAsLiteralString() throws Exception {
		assertThat(asLiteral("\n")).isEqualTo("\"\\n\"");
		assertThat(asLiteral("\r")).isEqualTo("\"\\r\"");
		assertThat(asLiteral("\'")).isEqualTo("\"'\"");
		assertThat(asLiteral("\"")).isEqualTo("\"\\\"\"");
		assertThat(asLiteral("\\")).isEqualTo("\"\\\\\"");
		assertThat(asLiteral(String.valueOf((char) 0x9))).isEqualTo("\"\\u0009\"");
		assertThat(asLiteral(String.valueOf((char) 0x19))).isEqualTo("\"\\u0019\"");
		assertThat(asLiteral(String.valueOf((char) 0x7f))).isEqualTo("\"\\u007f\"");
		assertThat(asLiteral(String.valueOf((char) 0x100))).isEqualTo("\"\\u0100\"");
		assertThat(asLiteral(String.valueOf((char) 0x1000))).isEqualTo("\"\\u1000\"");
		assertThat(asLiteral(" ")).isEqualTo("\" \"");
		assertThat(asLiteral("~")).isEqualTo("\"~\"");
		assertThat(asLiteral("a-zA-Z0-9")).isEqualTo("\"a-zA-Z0-9\"");
		assertThat(asLiteral((String) null)).isEqualTo("null");
	}

	@Test
	public void testAsLiteralFloat() throws Exception {
		assertThat(asLiteral(Float.POSITIVE_INFINITY)).isEqualTo("Float.POSITIVE_INFINITY");
		assertThat(asLiteral(Float.NEGATIVE_INFINITY)).isEqualTo("Float.NEGATIVE_INFINITY");
		assertThat(asLiteral(Float.NaN)).isEqualTo("Float.NaN");
		assertThat(asLiteral(0f)).isEqualTo("0.0f");
		assertThat(asLiteral(Float.MIN_VALUE)).isEqualTo("1.4E-45f");
		assertThat(asLiteral(Float.MAX_VALUE)).isEqualTo("3.4028235E38f");
	}

	@Test
	public void testAsLiteralDouble() throws Exception {
		assertThat(asLiteral(Double.POSITIVE_INFINITY)).isEqualTo("Double.POSITIVE_INFINITY");
		assertThat(asLiteral(Double.NEGATIVE_INFINITY)).isEqualTo("Double.NEGATIVE_INFINITY");
		assertThat(asLiteral(Double.NaN)).isEqualTo("Double.NaN");
		assertThat(asLiteral(0d)).isEqualTo("0.0");
		assertThat(asLiteral(Double.MIN_VALUE)).isEqualTo("4.9E-324");
		assertThat(asLiteral(Double.MAX_VALUE)).isEqualTo("1.7976931348623157E308");
	}

	@Test
	public void testAsLiteralOther() throws Exception {
		assertThat(asLiteral((Object) "String")).isEqualTo("\"String\"");
		assertThat(asLiteral((Object) 'c')).isEqualTo("'c'");
		assertThat(asLiteral((byte) 42)).isEqualTo("(byte) 42");
		assertThat(asLiteral((short) 43)).isEqualTo("(short) 43");
		assertThat(asLiteral((int) 44)).isEqualTo("44");
		assertThat(asLiteral((long) 45)).isEqualTo("45l");
		assertThat(asLiteral((Object) Float.NaN)).isEqualTo("Float.NaN");
		assertThat(asLiteral((Object) Double.NaN)).isEqualTo("Double.NaN");
		assertThat(asLiteral((Object) null)).isEqualTo("null");
		assertThat(asLiteral(new ArrayList<>())).isEqualTo("[]");
	}
}
