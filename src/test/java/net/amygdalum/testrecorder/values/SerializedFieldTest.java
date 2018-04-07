package net.amygdalum.testrecorder.values;

import static net.amygdalum.extensions.assertj.conventions.DefaultEquality.defaultEquality;
import static net.amygdalum.testrecorder.types.DeserializerContext.NULL;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.TestValueVisitor;
import net.amygdalum.testrecorder.util.testobjects.Annotated;
import net.amygdalum.testrecorder.util.testobjects.AnnotatedField;
import net.amygdalum.testrecorder.util.testobjects.MyAnnotation;
import net.amygdalum.testrecorder.util.testobjects.NoAnnotation;

public class SerializedFieldTest {

	@Test
	public void testGetName() throws Exception {
		assertThat(new SerializedField(null, "field", String.class, literal("stringvalue")).getName()).isEqualTo("field");
	}

	@Test
	public void testGetType() throws Exception {
		assertThat(new SerializedField(null, "field", String.class, literal("stringvalue")).getType()).isEqualTo(String.class);
	}

	@Test
	public void testGetValue() throws Exception {
		assertThat(new SerializedField(null, "field", String.class, literal("stringvalue")).getValue()).isEqualTo(literal("stringvalue"));
	}

	@Test
	public void testAccept() throws Exception {
		assertThat(new SerializedField(null, "f", String.class, literal("sv"))
			.accept(new TestValueVisitor(), NULL)).isEqualTo("field");
	}

	@Test
	public void testToString() throws Exception {
		assertThat(new SerializedField(Object.class, "f", String.class, literal("sv")).toString()).isEqualTo("java.lang.String f: sv");
	}

	@Test
	public void testGetDeclaringClass() throws Exception {
		assertThat(new SerializedField(Object.class, "f", String.class, literal("sv")).getDeclaringClass()).isEqualTo(Object.class);
	}

	@Test
	public void testGetAnnotations() throws Exception {
		SerializedField fieldAnnotated = new SerializedField(AnnotatedField.class, "annotated", String.class, nullInstance(String.class));

		assertThat(fieldAnnotated.getAnnotations()).containsExactly((Annotation) Annotated.class.getAnnotation(MyAnnotation.class));
		assertThat(fieldAnnotated.getAnnotation(MyAnnotation.class).get()).isEqualTo(Annotated.class.getAnnotation(MyAnnotation.class));
		assertThat(fieldAnnotated.getAnnotation(NoAnnotation.class).isPresent()).isFalse();

		SerializedField valueAnnotated = new SerializedField(AnnotatedField.class, "annotatedValue", Annotated.class, nullInstance(Annotated.class));

		assertThat(valueAnnotated.getAnnotations()).isEmpty();
		assertThat(valueAnnotated.getAnnotation(MyAnnotation.class).isPresent()).isFalse();
		assertThat(valueAnnotated.getAnnotation(NoAnnotation.class).isPresent()).isFalse();
	}

	@Test
	public void testEquals() throws Exception {
		assertThat(new SerializedField(Object.class, "f", String.class, literal("sv"))).satisfies(defaultEquality()
			.andEqualTo(new SerializedField(Object.class, "f", String.class, literal("sv")))
			.andNotEqualTo(new SerializedField(String.class, "f", String.class, literal("sv")))
			.andNotEqualTo(new SerializedField(Object.class, "nf", String.class, literal("sv")))
			.andNotEqualTo(new SerializedField(Object.class, "f", Object.class, literal("sv")))
			.andNotEqualTo(new SerializedField(Object.class, "f", String.class, literal("nsv")))
			.conventions());
	}

}
