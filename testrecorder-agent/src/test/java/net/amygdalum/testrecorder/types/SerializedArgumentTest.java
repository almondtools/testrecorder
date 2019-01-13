package net.amygdalum.testrecorder.types;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.MyAnnotation;

public class SerializedArgumentTest {

	@Test
	void testGetIndex() throws Exception {
		assertThat(new SerializedArgument(0, MyObject.methodSignature(), literal("stringvalue")).getIndex()).isEqualTo(0);
		assertThat(new SerializedArgument(1, MyObject.methodSignature(), literal(2)).getIndex()).isEqualTo(1);
	}

	@Test
	void testGetType() throws Exception {
		assertThat(new SerializedArgument(0, MyObject.methodSignature(), literal("stringvalue")).getType()).isEqualTo(String.class);
		assertThat(new SerializedArgument(1, MyObject.methodSignature(), literal("stringvalue")).getType()).isEqualTo(int.class);
	}

	@Test
	void testGetValue() throws Exception {
		assertThat(new SerializedArgument(0, MyObject.methodSignature(), literal("stringvalue")).getValue()).isEqualTo(literal("stringvalue"));
		assertThat(new SerializedArgument(0, MyObject.methodSignature(), literal(2)).getValue()).isEqualTo(literal(2));
	}

	@Test
	void testAccept() throws Exception {
		assertThat(new SerializedArgument(0, MyObject.methodSignature(), literal("stringvalue"))
			.accept(new TestValueVisitor())).isEqualTo("argument0");
	}

	@Test
	void testToString() throws Exception {
		assertThat(new SerializedArgument(0, MyObject.methodSignature(), literal("stringvalue")).toString()).isEqualTo("(java.lang.String string0: stringvalue)");
	}

	@Test
	void testEquals() throws Exception {
		assertThat(new SerializedArgument(0, MyObject.methodSignature(), literal("stringvalue"))).satisfies(defaultEquality()
			.andEqualTo(new SerializedArgument(0, MyObject.methodSignature(), literal("stringvalue")))
			.andNotEqualTo(new SerializedArgument(1, MyObject.methodSignature(), literal("stringvalue")))
			.andNotEqualTo(new SerializedArgument(0, MyObject.otherSignature(), literal("stringvalue")))
			.andNotEqualTo(new SerializedArgument(0, MyObject.methodSignature(), literal(1)))
			.conventions());
	}

	@Test
	void testCompareTo() throws Exception {
		assertThat(Stream.of(
			new SerializedArgument(0, MyObject.methodSignature(), literal("stringvalue")),
			new SerializedArgument(1, MyObject.methodSignature(), literal("stringvalue"))).sorted())
				.containsExactly(
					new SerializedArgument(0, MyObject.methodSignature(), literal("stringvalue")),
					new SerializedArgument(1, MyObject.methodSignature(), literal("stringvalue")));
		assertThat(Stream.of(
			new SerializedArgument(1, MyObject.methodSignature(), literal("stringvalue")),
			new SerializedArgument(0, MyObject.methodSignature(), literal("stringvalue"))).sorted())
				.containsExactly(
					new SerializedArgument(0, MyObject.methodSignature(), literal("stringvalue")),
					new SerializedArgument(1, MyObject.methodSignature(), literal("stringvalue")));
	}

	public MyAnnotation anno() {
		return new MyAnnotation() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return MyAnnotation.class;
			}
		};
	}

	public static class MyObject {

		public static MethodSignature methodSignature() {
			return new MethodSignature(MyObject.class, String.class, "method", new Type[] { String.class, int.class });
		}

		public String method(String arg1, int arg2) {
			return null;
		}

		public static MethodSignature otherSignature() {
			return new MethodSignature(MyObject.class, String.class, "other", new Type[] { String.class, int.class });
		}

		public String other(String arg1, int arg2) {
			return null;
		}
	}

}
