package net.amygdalum.testrecorder.values;

import static com.almondtools.conmatch.conventions.EqualityMatcher.satisfiesDefaultEquality;
import static net.amygdalum.testrecorder.deserializers.DefaultDeserializerContext.NULL;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Annotation;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.deserializers.TestValueVisitor;
import net.amygdalum.testrecorder.util.testobjects.Annotated;
import net.amygdalum.testrecorder.util.testobjects.AnnotatedField;
import net.amygdalum.testrecorder.util.testobjects.MyAnnotation;
import net.amygdalum.testrecorder.util.testobjects.NoAnnotation;

public class SerializedFieldTest {

	@Test
	public void testGetName() throws Exception {
		assertThat(new SerializedField(null, "field", String.class, literal("stringvalue")).getName(), equalTo("field"));
	}

	@Test
	public void testGetType() throws Exception {
		assertThat(new SerializedField(null, "field", String.class, literal("stringvalue")).getType(), equalTo(String.class));
	}

	@Test
	public void testGetValue() throws Exception {
		assertThat(new SerializedField(null, "field", String.class, literal("stringvalue")).getValue(), equalTo(literal("stringvalue")));
	}

	@Test
	public void testAccept() throws Exception {
		assertThat(new SerializedField(null, "f", String.class, literal("sv"))
			.accept(new TestValueVisitor(), NULL), equalTo("field"));
	}

	@Test
	public void testToString() throws Exception {
		assertThat(new SerializedField(Object.class, "f", String.class, literal("sv")).toString(), equalTo("java.lang.String f: sv"));
	}

	@Test
	public void testGetDeclaringClass() throws Exception {
		assertThat(new SerializedField(Object.class, "f", String.class, literal("sv")).getDeclaringClass(), equalTo(Object.class));
	}

	@Test
	public void testGetAnnotations() throws Exception {
		SerializedField fieldAnnotated = new SerializedField(AnnotatedField.class, "annotated", String.class, nullInstance(String.class));

		assertThat(fieldAnnotated.getAnnotations(), arrayContaining((Annotation) Annotated.class.getAnnotation(MyAnnotation.class)));
		assertThat(fieldAnnotated.getAnnotation(MyAnnotation.class).get(), equalTo(Annotated.class.getAnnotation(MyAnnotation.class)));
		assertThat(fieldAnnotated.getAnnotation(NoAnnotation.class).isPresent(), is(false));

		SerializedField valueAnnotated = new SerializedField(AnnotatedField.class, "annotatedValue", Annotated.class, nullInstance(Annotated.class));

		assertThat(valueAnnotated.getAnnotations(), emptyArray());
		assertThat(valueAnnotated.getAnnotation(MyAnnotation.class).isPresent(), is(false));
		assertThat(valueAnnotated.getAnnotation(NoAnnotation.class).isPresent(), is(false));
}

	@Test
	public void testEquals() throws Exception {
		assertThat(new SerializedField(Object.class, "f", String.class, literal("sv")), satisfiesDefaultEquality()
			.andEqualTo(new SerializedField(Object.class, "f", String.class, literal("sv")))
			.andNotEqualTo(new SerializedField(String.class, "f", String.class, literal("sv")))
			.andNotEqualTo(new SerializedField(Object.class, "nf", String.class, literal("sv")))
			.andNotEqualTo(new SerializedField(Object.class, "f", Object.class, literal("sv")))
			.andNotEqualTo(new SerializedField(Object.class, "f", String.class, literal("nsv"))));
	}

}
