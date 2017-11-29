package net.amygdalum.testrecorder;

import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.collection.IsArrayContaining.hasItemInArray;
import static org.junit.Assert.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedObject;

public class ContextSnapshotTest {

	@Test
	public void testMethodSnapshot() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		assertThat(snapshot.isValid(), is(true));
		assertThat(snapshot.getDeclaringClass(), equalTo(ArrayList.class));
		assertThat(snapshot.getResultType(), equalTo(boolean.class));
		assertThat(snapshot.getMethodName(), equalTo("add"));
		assertThat(snapshot.getArgumentTypes(), hasItemInArray(Object.class));
	}

	@Test
	public void testInvalidate() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.invalidate();

		assertThat(snapshot.isValid(), is(false));
	}

	@Test
	public void testGetThisType() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedList setupThis = new SerializedList(ArrayList.class).withResult(List.class);
		setupThis.add(literal("setup"));

		snapshot.setSetupThis(setupThis);

		assertThat(snapshot.getThisType(), equalTo(ArrayList.class));
	}

	@Test
	public void testSetGetSetupThis() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedList setupThis = new SerializedList(ArrayList.class).withResult(List.class);
		setupThis.add(literal("setup"));

		snapshot.setSetupThis(setupThis);

		assertThat(snapshot.getSetupThis(), equalTo(setupThis));
	}

	@Test
	public void testSetGetExpectThis() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedList expectedThis = new SerializedList(ArrayList.class).withResult(List.class);
		expectedThis.add(literal("expected"));

		snapshot.setExpectThis(expectedThis);

		assertThat(snapshot.getExpectThis(), equalTo(expectedThis));
	}

	@Test
	public void testSetGetSetupArgs() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.setSetupArgs(literal("a"), literal("b"));

		assertThat(snapshot.getSetupArgs(), arrayContaining(literal("a"), literal("b")));
	}

	@Test
	public void testSetGetExpectArgs() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.setExpectArgs(literal("c"), literal("d"));

		assertThat(snapshot.getExpectArgs(), arrayContaining(literal("c"), literal("d")));
	}

	@Test
	public void testSetGetExpectResult() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.setExpectResult(literal(boolean.class, true));

		assertThat(snapshot.getExpectResult(), equalTo(literal(boolean.class, true)));
	}

	@Test
	public void testSetGetExpectException() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedObject expectException = new SerializedObject(NullPointerException.class);

		snapshot.setExpectException(expectException);

		assertThat(snapshot.getExpectException(), sameInstance(expectException));
	}

	@Test
	public void testGetTime() throws Exception {
		assertThat(new ContextSnapshot(0l, "key", new MethodSignature(Object.class, new Annotation[0], Object.class, "method", new Annotation[0][0], new Type[0])).getTime(), equalTo(0l));
		assertThat(new ContextSnapshot(1l, "key", new MethodSignature(Object.class, new Annotation[0], Object.class, "method", new Annotation[0][0], new Type[0])).getTime(), equalTo(1l));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetAnnotation() throws Exception {
		ContextSnapshot snapshot = new ContextSnapshot(0l, "key", new MethodSignature(
			Object.class,
			new Annotation[] { anno("result") },
			Object.class,
			"method",
			new Annotation[][] { new Annotation[] { anno("arg") } },
			new Type[] { Integer.class }));

		assertThat(snapshot.getResultAnnotation(), arrayContaining(instanceOf(Anno.class)));
		assertThat(snapshot.getMethodAnnotation(Anno.class).get().value(), equalTo("result"));
		assertThat(snapshot.getMethodAnnotation(NoAnno.class).isPresent(), is(false));
		assertThat(((Anno) snapshot.getResultAnnotation()[0]).value(), equalTo("result"));
		assertThat(snapshot.getArgumentAnnotations(), arrayWithSize(1));
		assertThat(snapshot.getArgumentAnnotations()[0], arrayContaining(instanceOf(Anno.class)));
		assertThat(((Anno) snapshot.getArgumentAnnotations()[0][0]).value(), equalTo("arg"));
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

		assertThat(snapshot.getAnnotatedSetupArgs(), arrayWithSize(1));
		assertThat(snapshot.getAnnotatedSetupArgs()[0].getAnnotation(Anno.class).get().value(), equalTo("arg"));
		assertThat(snapshot.getAnnotatedSetupArgs()[0].getAnnotation(NoAnno.class).isPresent(), is(false));
		assertThat(snapshot.getAnnotatedSetupArgs()[0].value, equalTo(literal(int.class, 42)));
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

		assertThat(snapshot.getAnnotatedExpectArgs(), arrayWithSize(1));
		assertThat(snapshot.getAnnotatedExpectArgs()[0].getAnnotation(Anno.class).get().value(), equalTo("arg"));
		assertThat(snapshot.getAnnotatedExpectArgs()[0].getAnnotation(NoAnno.class).isPresent(), is(false));
		assertThat(snapshot.getAnnotatedExpectArgs()[0].value, equalTo(literal(int.class, 42)));
	}

	@Test
	public void testToString() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(Object.class, String.class, "method", Integer.class);

		assertThat(snapshot.toString(), containsString("Object"));
		assertThat(snapshot.toString(), containsString("String"));
		assertThat(snapshot.toString(), containsString("method"));
		assertThat(snapshot.toString(), containsString("Integer"));
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
