package net.amygdalum.testrecorder;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedObject;

public class ContextSnapshotTest {

	@Test
	public void testMethodSnapshot() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		assertThat(snapshot.isValid()).isTrue();
		assertThat(snapshot.getDeclaringClass()).isEqualTo(ArrayList.class);
		assertThat(snapshot.getResultType()).isEqualTo(boolean.class);
		assertThat(snapshot.getMethodName()).isEqualTo("add");
		assertThat(snapshot.getArgumentTypes()).contains(Object.class);
	}

	@Test
	public void testInvalidate() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.invalidate();

		assertThat(snapshot.isValid()).isFalse();
	}

	@Test
	public void testGetThisType() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedList setupThis = new SerializedList(ArrayList.class);
		setupThis.useAs(List.class);
		setupThis.add(literal("setup"));

		snapshot.setSetupThis(setupThis);

		assertThat(snapshot.getThisType()).isEqualTo(ArrayList.class);
	}

	@Test
	public void testSetGetSetupThis() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedList setupThis = new SerializedList(ArrayList.class);
		setupThis.useAs(List.class);
		setupThis.add(literal("setup"));

		snapshot.setSetupThis(setupThis);

		assertThat(snapshot.getSetupThis()).isEqualTo(setupThis);
	}

	@Test
	public void testSetGetExpectThis() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedList expectedThis = new SerializedList(ArrayList.class);
		expectedThis.useAs(List.class);
		expectedThis.add(literal("expected"));

		snapshot.setExpectThis(expectedThis);

		assertThat(snapshot.getExpectThis()).isEqualTo(expectedThis);
	}

	@Test
	public void testSetGetSetupArgs() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.setSetupArgs(literal("a"), literal("b"));

		assertThat(snapshot.getSetupArgs()).containsExactly(literal("a"), literal("b"));
	}

	@Test
	public void testSetGetExpectArgs() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.setExpectArgs(literal("c"), literal("d"));

		assertThat(snapshot.getExpectArgs()).containsExactly(literal("c"), literal("d"));
	}

	@Test
	public void testSetGetExpectResult() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.setExpectResult(literal(boolean.class, true));

		assertThat(snapshot.getExpectResult()).isEqualTo(literal(boolean.class, true));
	}

	@Test
	public void testSetGetExpectException() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedObject expectException = new SerializedObject(NullPointerException.class);

		snapshot.setExpectException(expectException);

		assertThat(snapshot.getExpectException()).isSameAs(expectException);
	}

	@Test
	public void testGetTime() throws Exception {
		assertThat(new ContextSnapshot(0l, "key", new MethodSignature(Object.class, new Annotation[0], Object.class, "method", new Annotation[0][0], new Type[0])).getTime()).isEqualTo(0l);
		assertThat(new ContextSnapshot(1l, "key", new MethodSignature(Object.class, new Annotation[0], Object.class, "method", new Annotation[0][0], new Type[0])).getTime()).isEqualTo(1l);
	}

	@Test
	public void testGetAnnotation() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(0l, "key", new MethodSignature(
			Object.class,
			new Annotation[] { anno("result") },
			Object.class,
			"method",
			new Annotation[][] { new Annotation[] { anno("arg") } },
			new Type[] { Integer.class }));

		assertThat(snapshot.getResultAnnotation())
			.hasSize(1)
			.hasOnlyElementsOfTypes(Anno.class);
		assertThat(snapshot.getMethodAnnotation(Anno.class).get().value()).isEqualTo("result");
		assertThat(snapshot.getMethodAnnotation(NoAnno.class).isPresent()).isFalse();
		assertThat(((Anno) snapshot.getResultAnnotation()[0]).value()).isEqualTo("result");
		assertThat(snapshot.getArgumentAnnotations()).hasSize(1);
		assertThat(snapshot.getArgumentAnnotations()[0])
			.hasSize(1)
			.hasOnlyElementsOfTypes(Anno.class);
		assertThat(((Anno) snapshot.getArgumentAnnotations()[0][0]).value()).isEqualTo("arg");
	}

	@Test
	public void testGetAnnotatedSetupArgs() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(0l, "key", new MethodSignature(
			Object.class,
			new Annotation[] { anno("result") },
			String.class,
			"method",
			new Annotation[][] { new Annotation[] { anno("arg") } },
			new Type[] { Integer.class }));

		snapshot.setSetupArgs(literal(int.class, 42));

		assertThat(snapshot.getAnnotatedSetupArgs()).hasSize(1);
		assertThat(snapshot.getAnnotatedSetupArgs()[0].getAnnotation(Anno.class).get().value()).isEqualTo("arg");
		assertThat(snapshot.getAnnotatedSetupArgs()[0].getAnnotation(NoAnno.class).isPresent()).isFalse();
		assertThat(snapshot.getAnnotatedSetupArgs()[0].value).isEqualTo(literal(int.class, 42));
	}

	@Test
	public void testGetAnnotatedExpectArgs() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(0l, "key", new MethodSignature(
			Object.class,
			new Annotation[] { anno("result") },
			String.class,
			"method",
			new Annotation[][] { new Annotation[] { anno("arg") } },
			new Type[] { Integer.class }));

		snapshot.setExpectArgs(literal(int.class, 42));

		assertThat(snapshot.getAnnotatedExpectArgs()).hasSize(1);
		assertThat(snapshot.getAnnotatedExpectArgs()[0].getAnnotation(Anno.class).get().value()).isEqualTo("arg");
		assertThat(snapshot.getAnnotatedExpectArgs()[0].getAnnotation(NoAnno.class).isPresent()).isFalse();
		assertThat(snapshot.getAnnotatedExpectArgs()[0].value).isEqualTo(literal(int.class, 42));
	}

	@Test
	public void testToString() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(Object.class, String.class, "method", Integer.class);

		assertThat(snapshot.toString()).contains("Object", "String", "method", "Integer");
	}

	private ContextSnapshot contextSnapshot(Class<?> declaringClass, Type resultType, String methodName, Type... argumentTypes) {
		return new ContextSnapshot(0, "key", new MethodSignature(declaringClass, new Annotation[0], resultType, methodName, new Annotation[0][0], argumentTypes));
	}

	private Anno anno(String value) {
		return new Anno() {

			@Override
			public String value() {
				return value;
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return Anno.class;
			}

		};
	}

	@interface Anno {
		String value();
	}

	@interface NoAnno {
		String value();
	}

}
