package net.amygdalum.testrecorder.util;

import static net.amygdalum.extensions.assertj.conventions.UtilityClass.utilityClass;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LiteralsTest {

	@Test
	void testLiterals() throws Exception {
		assertThat(Literals.class).satisfies(utilityClass().conventions());
	}

	@Test
	void testClassOf() throws Exception {
		assertThat(Literals.classOf("MyClass")).isEqualTo("MyClass.class");
	}

	@Nested
	class testAsLiteral {
		@Test
		void forCharacter() throws Exception {
			assertThat(Literals.asLiteral('a')).isEqualTo("'a'");
			assertThat(Literals.asLiteral('\n')).isEqualTo("'\\n'");
			assertThat(Literals.asLiteral('\r')).isEqualTo("'\\r'");
			assertThat(Literals.asLiteral('\t')).isEqualTo("'\\t'");
			assertThat(Literals.asLiteral('\\')).isEqualTo("'\\\\'");
			assertThat(Literals.asLiteral('\'')).isEqualTo("'\\''");
			assertThat(Literals.asLiteral('"')).isEqualTo("'\"'");
			assertThat(Literals.asLiteral('\u0000')).isEqualTo("'\\u0000'");
			assertThat(Literals.asLiteral('\u001f')).isEqualTo("'\\u001f'");
			assertThat(Literals.asLiteral('\u0080')).isEqualTo("'\\u0080'");
			assertThat(Literals.asLiteral('\u0100')).isEqualTo("'\\u0100'");
			assertThat(Literals.asLiteral('\uffff')).isEqualTo("'\\uffff'");
		}

		@Test
		void forString() throws Exception {
			assertThat(Literals.asLiteral((String) null)).isEqualTo("null");
			assertThat(Literals.asLiteral("")).isEqualTo("\"\"");
			assertThat(Literals.asLiteral("a")).isEqualTo("\"a\"");
			assertThat(Literals.asLiteral("\n")).isEqualTo("\"\\n\"");
			assertThat(Literals.asLiteral("\r")).isEqualTo("\"\\r\"");
			assertThat(Literals.asLiteral("\t")).isEqualTo("\"\\t\"");
			assertThat(Literals.asLiteral("\\")).isEqualTo("\"\\\\\"");
			assertThat(Literals.asLiteral("\"")).isEqualTo("\"\\\"\"");
			assertThat(Literals.asLiteral("'")).isEqualTo("\"'\"");
			assertThat(Literals.asLiteral("\u0000")).isEqualTo("\"\\u0000\"");
			assertThat(Literals.asLiteral("\u001f")).isEqualTo("\"\\u001f\"");
			assertThat(Literals.asLiteral("\u0080")).isEqualTo("\"\\u0080\"");
			assertThat(Literals.asLiteral("\u0100")).isEqualTo("\"\\u0100\"");
			assertThat(Literals.asLiteral("\uffff")).isEqualTo("\"\\uffff\"");
		}

		@Test
		void forFloat() throws Exception {
			assertThat(Literals.asLiteral(0.123f)).isEqualTo("0.123f");
			assertThat(Literals.asLiteral(Float.NaN)).isEqualTo("Float.NaN");
			assertThat(Literals.asLiteral(Float.NEGATIVE_INFINITY)).isEqualTo("Float.NEGATIVE_INFINITY");
			assertThat(Literals.asLiteral(Float.POSITIVE_INFINITY)).isEqualTo("Float.POSITIVE_INFINITY");
		}

		@Test
		void forDouble() throws Exception {
			assertThat(Literals.asLiteral(1.23E-123)).isEqualTo("1.23E-123");
			assertThat(Literals.asLiteral(Double.NaN)).isEqualTo("Double.NaN");
			assertThat(Literals.asLiteral(Double.NEGATIVE_INFINITY)).isEqualTo("Double.NEGATIVE_INFINITY");
			assertThat(Literals.asLiteral(Double.POSITIVE_INFINITY)).isEqualTo("Double.POSITIVE_INFINITY");
		}

		@Test
		public void forObject() throws Exception {
			assertThat(Literals.asLiteral((Object) null)).isEqualTo("null");
			assertThat(Literals.asLiteral((Object) "string")).isEqualTo("\"string\"");
			assertThat(Literals.asLiteral((Object) 'c')).isEqualTo("'c'");
			assertThat(Literals.asLiteral(Byte.valueOf((byte) 1))).isEqualTo("(byte) 1");
			assertThat(Literals.asLiteral(Short.valueOf((short) 2))).isEqualTo("(short) 2");
			assertThat(Literals.asLiteral(Integer.valueOf((int) 3))).isEqualTo("3");
			assertThat(Literals.asLiteral(Long.valueOf((long) 4))).isEqualTo("4l");
			assertThat(Literals.asLiteral((Object) 1.1f)).isEqualTo("1.1f");
			assertThat(Literals.asLiteral((Object) 1.23E-123)).isEqualTo("1.23E-123");
			assertThat(Literals.asLiteral(new Object() {
				@Override
				public String toString() {
					return "toString";
				}
			})).isEqualTo("toString");
		}
	}
}
