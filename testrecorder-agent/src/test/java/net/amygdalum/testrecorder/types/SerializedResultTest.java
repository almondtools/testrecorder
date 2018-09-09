package net.amygdalum.testrecorder.types;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.MyAnnotation;

public class SerializedResultTest {

	@Test
	void testGetType() throws Exception {
		assertThat(new SerializedResult(String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue")).getType()).isEqualTo(String.class);
		assertThat(new SerializedResult(Object.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue")).getType()).isEqualTo(Object.class);
	}

	@Test
	void testGetValue() throws Exception {
		assertThat(new SerializedResult(String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue")).getValue()).isEqualTo(literal("stringvalue"));
		assertThat(new SerializedResult(String.class, SerializedRole.NO_ANNOTATIONS, literal(2)).getValue()).isEqualTo(literal(2));
	}

	@Test
	void testAccept() throws Exception {
		assertThat(new SerializedResult(String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue"))
			.accept(new TestValueVisitor())).isEqualTo("result");
	}

	@Test
	void testToString() throws Exception {
		assertThat(new SerializedResult(String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue")).toString()).isEqualTo("=>java.lang.String: stringvalue");
	}

	@Test
	void testGetAnnotations() throws Exception {
		assertThat(new SerializedResult(String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue")).getAnnotations()).isEmpty();
		MyAnnotation anno = anno();
		assertThat(new SerializedResult(String.class, new Annotation[] { anno }, literal("stringvalue")).getAnnotations()).containsExactly(anno);
	}

	@Test
	void testEquals() throws Exception {
		assertThat(new SerializedResult(String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue"))).satisfies(defaultEquality()
			.andEqualTo(new SerializedResult(String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue")))
			.andEqualTo(new SerializedResult(String.class, new Annotation[] {anno()}, literal("stringvalue")))
			.andNotEqualTo(new SerializedResult(Object.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue")))
			.andNotEqualTo(new SerializedResult(String.class, SerializedRole.NO_ANNOTATIONS, literal(3)))
			.conventions());
	}

	@Test
	public void testCompareTo() throws Exception {
		assertThat(Stream.of(
			new SerializedArgument(0, String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue")),
			new SerializedArgument(1, String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue"))).sorted())
				.containsExactly(
					new SerializedArgument(0, String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue")),
					new SerializedArgument(1, String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue")));
		assertThat(Stream.of(
			new SerializedArgument(1, String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue")),
			new SerializedArgument(0, String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue"))).sorted())
				.containsExactly(
					new SerializedArgument(0, String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue")),
					new SerializedArgument(1, String.class, SerializedRole.NO_ANNOTATIONS, literal("stringvalue")));
	}

	public MyAnnotation anno() {
		return new MyAnnotation() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return MyAnnotation.class;
			}
		};
	}

}
