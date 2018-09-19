package net.amygdalum.testrecorder.deserializers;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.FieldSignature;
import net.amygdalum.testrecorder.types.SerializedField;
import net.amygdalum.testrecorder.values.SerializedLiteral;

public class DefaultDeserializerContextTest {

	@Test
	public void testGetHints() throws Exception {
		DefaultDeserializerContext context = new DefaultDeserializerContext();
		SerializedLiteral value = SerializedLiteral.literal("str");
		context.addHint(String.class, hint2("value2"));

		assertThat(context.getHints(value, Hint1.class).toArray(Hint1[]::new)).isEmpty();
		assertThat(context.getHints(value, Hint2.class).toArray(Hint2[]::new)).extracting(hint -> hint.name()).containsExactly("value2");
	}

	@Test
	public void testGetHint() throws Exception {
		DefaultDeserializerContext context = new DefaultDeserializerContext();
		SerializedLiteral value = SerializedLiteral.literal(1);
		context.addHint(Integer.class, hint1("value1"));

		assertThat(context.getHint(value, Hint2.class).isPresent()).isFalse();
		assertThat(context.getHint(value, Hint1.class)).map(hint -> hint.name()).contains("value1");
	}

	@Test
	public void testGetFieldHints() throws Exception {
		DefaultDeserializerContext context = new DefaultDeserializerContext();
		SerializedLiteral value = SerializedLiteral.literal(1);
		SerializedField field = new SerializedField(new FieldSignature(MyObject.class, String.class, "field"), value);
		context.addHint(field.getDeclaringClass().getDeclaredField("field"), hint2("value2"));

		assertThat(context.getHints(field, Hint1.class).toArray(Hint1[]::new)).isEmpty();
		assertThat(context.getHints(field, Hint2.class).toArray(Hint2[]::new)).extracting(hint -> hint.name()).containsExactly("value2");
	}

	@Test
	public void testGetFieldHint() throws Exception {
		DefaultDeserializerContext context = new DefaultDeserializerContext();
		SerializedLiteral value = SerializedLiteral.literal(1);
		SerializedField field = new SerializedField(new FieldSignature(MyObject.class, String.class, "field"), value);
		context.addHint(field.getDeclaringClass().getDeclaredField("field"), hint1("value1"));

		assertThat(context.getHint(field, Hint2.class).isPresent()).isFalse();
		assertThat(context.getHint(field, Hint1.class)).map(hint -> hint.name()).contains("value1");
	}

	private Hint1 hint1(String name) {
		return new Hint1() {

			@Override
			public String name() {
				return name;
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return Hint1.class;
			}
		};
	}

	private Hint2 hint2(String name) {
		return new Hint2() {

			@Override
			public String name() {
				return name;
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return Hint1.class;
			}
		};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
	public @interface Hint1 {
		String name();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
	public @interface Hint2 {
		String name();
	}

	public static class MyObject {
		String field;
	}

}
