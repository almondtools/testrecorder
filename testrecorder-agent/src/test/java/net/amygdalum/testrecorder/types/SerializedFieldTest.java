package net.amygdalum.testrecorder.types;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.util.testobjects.Annotated;
import net.amygdalum.testrecorder.util.testobjects.AnnotatedField;
import net.amygdalum.testrecorder.util.testobjects.MyAnnotation;

public class SerializedFieldTest {

	@Test
	void testGetName() throws Exception {
		assertThat(new SerializedField(null, "field", String.class, literal("stringvalue")).getName()).isEqualTo("field");
	}

	@Test
	void testGetType() throws Exception {
		assertThat(new SerializedField(null, "field", String.class, literal("stringvalue")).getType()).isEqualTo(String.class);
	}

	@Test
	void testGetValue() throws Exception {
		assertThat(new SerializedField(null, "field", String.class, literal("stringvalue")).getValue()).isEqualTo(literal("stringvalue"));
	}

	@Test
	void testAccept() throws Exception {
		assertThat(new SerializedField(null, "f", String.class, literal("sv"))
			.accept(new TestValueVisitor())).isEqualTo("field");
	}

	@Test
	void testToString() throws Exception {
		assertThat(new SerializedField(Object.class, "f", String.class, literal("sv")).toString()).isEqualTo("java.lang.String f: sv");
	}

	@Test
	void testGetDeclaringClass() throws Exception {
		assertThat(new SerializedField(Object.class, "f", String.class, literal("sv")).getDeclaringClass()).isEqualTo(Object.class);
	}

	@Test
	void testGetAnnotations() throws Exception {
		SerializedField fieldAnnotated = new SerializedField(AnnotatedField.class, "annotated", String.class, nullInstance());

		assertThat(fieldAnnotated.getAnnotations()).containsExactly((Annotation) Annotated.class.getAnnotation(MyAnnotation.class));

		SerializedField valueAnnotated = new SerializedField(AnnotatedField.class, "annotatedValue", Annotated.class, nullInstance());

		assertThat(valueAnnotated.getAnnotations()).isEmpty();
	}

	@Test
	void testEquals() throws Exception {
		assertThat(new SerializedField(Object.class, "f", String.class, literal("sv"))).satisfies(defaultEquality()
			.andEqualTo(new SerializedField(Object.class, "f", String.class, literal("sv")))
			.andNotEqualTo(new SerializedField(String.class, "f", String.class, literal("sv")))
			.andNotEqualTo(new SerializedField(Object.class, "nf", String.class, literal("sv")))
			.andNotEqualTo(new SerializedField(Object.class, "f", Object.class, literal("sv")))
			.andNotEqualTo(new SerializedField(Object.class, "f", String.class, literal("nsv")))
			.conventions());
	}

	@Test
	public void testCompareTo() throws Exception {
		assertThat(Stream.of(
			new SerializedField(null, "a", String.class, literal("stringvalue")),
			new SerializedField(null, "b", String.class, literal("stringvalue"))).sorted())
		.containsExactly(
			new SerializedField(null, "a", String.class, literal("stringvalue")),
			new SerializedField(null, "b", String.class, literal("stringvalue")));
		assertThat(Stream.of(
			new SerializedField(null, "b", String.class, literal("stringvalue")),
			new SerializedField(null, "a", String.class, literal("stringvalue"))).sorted())
		.containsExactly(
			new SerializedField(null, "a", String.class, literal("stringvalue")),
			new SerializedField(null, "b", String.class, literal("stringvalue")));
	}

}
