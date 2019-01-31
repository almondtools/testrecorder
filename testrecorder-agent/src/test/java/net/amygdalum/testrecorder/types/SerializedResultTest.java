package net.amygdalum.testrecorder.types;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.lang.reflect.Type;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SerializedResultTest {

	@Nested
	class testSerializedResult {

		@Test
		public void onSignatureNull() throws Exception {
			assertThatCode(() -> new SerializedResult(null, literal("stringvalue")))
				.isInstanceOf(AssertionError.class);
		}

		@Test
		public void onValueNull() throws Exception {
			assertThatCode(() -> new SerializedResult(MyObject.stringSignature(), null))
				.isInstanceOf(AssertionError.class);
		}
	}

	@Test
	void testGetType() throws Exception {
		assertThat(new SerializedResult(MyObject.stringSignature(), literal("stringvalue")).getType()).isEqualTo(String.class);
		assertThat(new SerializedResult(MyObject.objectSignature(), literal("stringvalue")).getType()).isEqualTo(Object.class);
	}

	@Test
	void testGetValue() throws Exception {
		assertThat(new SerializedResult(MyObject.stringSignature(), literal("stringvalue")).getValue()).isEqualTo(literal("stringvalue"));
		assertThat(new SerializedResult(MyObject.objectSignature(), literal(2)).getValue()).isEqualTo(literal(2));
	}

	@Test
	void testAccept() throws Exception {
		assertThat(new SerializedResult(MyObject.stringSignature(), literal("stringvalue"))
			.accept(new TestValueVisitor())).isEqualTo("result");
	}

	@Test
	void testToString() throws Exception {
		assertThat(new SerializedResult(MyObject.stringSignature(), literal("stringvalue")).toString()).isEqualTo("=>java.lang.String: stringvalue");
	}

	@Test
	void testEquals() throws Exception {
		assertThat(new SerializedResult(MyObject.stringSignature(), literal("stringvalue"))).satisfies(defaultEquality()
			.andEqualTo(new SerializedResult(MyObject.stringSignature(), literal("stringvalue")))
			.andNotEqualTo(new SerializedResult(MyObject.objectSignature(), literal("stringvalue")))
			.andNotEqualTo(new SerializedResult(MyObject.stringSignature(), literal(3)))
			.conventions());
	}

	public static class MyObject {

		public static MethodSignature stringSignature() {
			return new MethodSignature(MyObject.class, String.class, "string", new Type[0]);
		}

		public String string() {
			return null;
		}

		public static MethodSignature objectSignature() {
			return new MethodSignature(MyObject.class, Object.class, "object", new Type[0]);
		}

		public Object object() {
			return null;
		}
	}

}
