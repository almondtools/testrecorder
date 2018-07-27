package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.toList;
import static net.amygdalum.testrecorder.values.SerializedLiteral.literal;
import static net.amygdalum.testrecorder.values.SerializedNull.nullInstance;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import org.junit.jupiter.api.Test;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.testobjects.Static;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedList;
import net.amygdalum.testrecorder.values.SerializedNull;
import net.amygdalum.testrecorder.values.SerializedObject;
import net.amygdalum.testrecorder.values.SerializedOutput;

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
	public void testGetKey() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		assertThat(snapshot.getKey()).isEqualTo("key");
		assertThat(snapshot.matches("key")).isTrue();
		assertThat(snapshot.matches("nokey")).isFalse();
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

		SerializedValue getValue = snapshot.getSetupThis();
		SerializedValue onValue = snapshot.onSetupThis().orElse(nullInstance());
		SerializedValue streamValue = snapshot.streamSetupThis().findFirst().orElse(nullInstance());

		assertThat(getValue).isSameAs(setupThis);
		assertThat(onValue).isSameAs(setupThis);
		assertThat(streamValue).isSameAs(setupThis);
	}

	@Test
	public void testSetGetSetupThisNull() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		SerializedValue getValue = snapshot.getSetupThis();
		SerializedValue onValue = snapshot.onSetupThis().orElse(nullInstance());
		SerializedValue streamValue = snapshot.streamSetupThis().findFirst().orElse(nullInstance());

		assertThat(getValue).isNull();
		assertThat(onValue).isInstanceOf(SerializedNull.class);
		assertThat(streamValue).isInstanceOf(SerializedNull.class);
	}

	@Test
	public void testSetGetExpectThis() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedList expectedThis = new SerializedList(ArrayList.class);
		expectedThis.useAs(List.class);
		expectedThis.add(literal("expected"));

		snapshot.setExpectThis(expectedThis);

		SerializedValue getValue = snapshot.getExpectThis();
		SerializedValue onValue = snapshot.onExpectThis().orElse(nullInstance());
		SerializedValue streamValue = snapshot.streamExpectThis().findFirst().orElse(nullInstance());

		assertThat(getValue).isEqualTo(expectedThis);
		assertThat(onValue).isEqualTo(expectedThis);
		assertThat(streamValue).isEqualTo(expectedThis);
	}

	@Test
	public void testSetGetExpectThisNull() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		SerializedValue getValue = snapshot.getExpectThis();
		SerializedValue onValue = snapshot.onExpectThis().orElse(nullInstance());
		SerializedValue streamValue = snapshot.streamExpectThis().findFirst().orElse(nullInstance());

		assertThat(getValue).isNull();
		assertThat(onValue).isInstanceOf(SerializedNull.class);
		assertThat(streamValue).isInstanceOf(SerializedNull.class);
	}

	@Test
	public void testSetGetSetupArgs() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.setSetupArgs(literal("a"), literal("b"));

		SerializedValue[] getValue = snapshot.getSetupArgs();
		SerializedValue[] streamValue = snapshot.streamSetupArgs().toArray(SerializedValue[]::new);

		assertThat(getValue).containsExactly(literal("a"), literal("b"));
		assertThat(streamValue).containsExactly(literal("a"), literal("b"));
	}

	@Test
	public void testSetGetExpectArgs() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.setExpectArgs(literal("c"), literal("d"));

		SerializedValue[] getValue = snapshot.getExpectArgs();
		SerializedValue[] streamValue = snapshot.streamExpectArgs().toArray(SerializedValue[]::new);

		assertThat(getValue).containsExactly(literal("c"), literal("d"));
		assertThat(streamValue).containsExactly(literal("c"), literal("d"));
	}

	@Test
	public void testSetGetExpectResult() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.setExpectResult(literal(boolean.class, true));

		SerializedValue getValue = snapshot.getExpectResult();
		SerializedValue onValue = snapshot.onExpectResult().orElse(nullInstance());
		SerializedValue streamValue = snapshot.streamExpectResult().findFirst().orElse(nullInstance());

		assertThat(getValue).isEqualTo(literal(boolean.class, true));
		assertThat(onValue).isEqualTo(literal(boolean.class, true));
		assertThat(streamValue).isEqualTo(literal(boolean.class, true));
	}

	@Test
	public void testSetGetExpectException() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedObject expectException = new SerializedObject(NullPointerException.class);

		snapshot.setExpectException(expectException);

		SerializedValue getValue = snapshot.getExpectException();
		SerializedValue onValue = snapshot.onExpectException().orElse(nullInstance());
		SerializedValue streamValue = snapshot.streamExpectException().findFirst().orElse(nullInstance());

		assertThat(getValue).isSameAs(expectException);
		assertThat(onValue).isSameAs(expectException);
		assertThat(streamValue).isSameAs(expectException);
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
	public void testSetGetSetupGlobals() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedField field = new SerializedField(Static.class, "global", String.class, literal("a"));

		snapshot.setSetupGlobals(field);

		SerializedField[] getValue = snapshot.getSetupGlobals();
		SerializedField[] streamValue = snapshot.streamSetupGlobals().toArray(SerializedField[]::new);

		assertThat(getValue).containsExactly(field);
		assertThat(streamValue).containsExactly(field);
	}

	@Test
	public void testSetGetExpectGlobals() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedField field = new SerializedField(Static.class, "global", String.class, literal("a"));

		snapshot.setExpectGlobals(field);

		SerializedField[] getValue = snapshot.getExpectGlobals();
		SerializedField[] streamValue = snapshot.streamExpectGlobals().toArray(SerializedField[]::new);

		assertThat(getValue).containsExactly(field);
		assertThat(streamValue).containsExactly(field);
	}

	@Test
	public void testSetupInput() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedInput input = new SerializedInput(41, String.class, "name", char.class, new Type[0]);

		snapshot.addInput(input);

		boolean hasInput = snapshot.hasSetupInput();
		boolean hasInput2 = snapshot.lastInputSatitisfies(t -> true);
		boolean hasInput3 = snapshot.lastInputSatitisfies(t -> false);
		Queue<SerializedInput> getValue = snapshot.getSetupInput();
		List<SerializedInput> streamValue = snapshot.streamInput().collect(toList());

		assertThat(hasInput).isTrue();
		assertThat(hasInput2).isTrue();
		assertThat(hasInput3).isFalse();
		assertThat(getValue).containsExactly(input);
		assertThat(streamValue).containsExactly(input);
	}

	@Test
	public void testSetupNoInput() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		boolean hasInput = snapshot.hasSetupInput();
		boolean hasInput2 = snapshot.lastInputSatitisfies(t -> true);
		Queue<SerializedInput> getValue = snapshot.getSetupInput();
		List<SerializedInput> streamValue = snapshot.streamInput().collect(toList());

		assertThat(hasInput).isFalse();
		assertThat(hasInput2).isFalse();
		assertThat(getValue).isEmpty();
		assertThat(streamValue).isEmpty();
	}

	@Test
	public void testExpectOutput() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedOutput output = new SerializedOutput(41, String.class, "name", char.class, new Type[0]);

		snapshot.addOutput(output);

		boolean hasOutput = snapshot.hasExpectOutput();
		boolean hasOutput2 = snapshot.lastOutputSatitisfies(t -> true);
		boolean hasOutput3 = snapshot.lastOutputSatitisfies(t -> false);
		Queue<SerializedOutput> getValue = snapshot.getExpectOutput();
		List<SerializedOutput> streamValue = snapshot.streamOutput().collect(toList());

		assertThat(hasOutput).isTrue();
		assertThat(hasOutput2).isTrue();
		assertThat(hasOutput3).isFalse();
		assertThat(getValue).containsExactly(output);
		assertThat(streamValue).containsExactly(output);
	}

	@Test
	public void testExpectNoOutput() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		boolean hasOutput = snapshot.hasExpectOutput();
		boolean hasOutput2 = snapshot.lastOutputSatitisfies(t -> true);
		Queue<SerializedOutput> getValue = snapshot.getExpectOutput();
		List<SerializedOutput> streamValue = snapshot.streamOutput().collect(toList());

		assertThat(hasOutput).isFalse();
		assertThat(hasOutput2).isFalse();
		assertThat(getValue).isEmpty();
		assertThat(streamValue).isEmpty();
	}

	@Test
	public void testToString() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(Object.class, String.class, "method", Integer.class);

		assertThat(snapshot.toString()).contains("Object", "String", "method", "Integer");
	}

	private ContextSnapshot contextSnapshot(Class<?> declaringClass, Type resultType, String methodName, Type... argumentTypes) {
		return new ContextSnapshot(0, "key", new MethodSignature(declaringClass, new Annotation[0], resultType, methodName, new Annotation[0][0], argumentTypes));
	}

	@Test
	public void testOnThis() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);
		SerializedList setupThis = new SerializedList(ArrayList.class);
		setupThis.useAs(List.class);
		setupThis.add(literal("setup"));

		snapshot.setSetupThis(setupThis);

		SerializedList expectThis = new SerializedList(ArrayList.class);
		expectThis.useAs(List.class);
		expectThis.add(literal("expect"));

		snapshot.setExpectThis(expectThis);

		assertThat(snapshot.onThis()
			.map((first, second) -> first.toString() + ":" + second.toString(), first -> "second missing", second -> "first missing"))
				.contains("[setup]:[expect]");
	}

	@Test
	public void testOnArgs() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.setSetupArgs(literal("a"), literal("b"));
		snapshot.setExpectArgs(literal("b"), literal("a"));

		assertThat(snapshot.onArgs()
			.map((first, second) -> Arrays.toString(first) + ":" + Arrays.toString(second), first -> "second missing", second -> "first missing"))
				.contains("[a, b]:[b, a]");

	}

	@Test
	public void testOnGlobals() throws Exception {
		ContextSnapshot snapshot = contextSnapshot(ArrayList.class, boolean.class, "add", Object.class);

		snapshot.setSetupGlobals(new SerializedField(Static.class, "global", String.class, literal("a")));
		snapshot.setExpectGlobals(new SerializedField(Static.class, "global", String.class, literal("b")));

		assertThat(snapshot.onGlobals()
			.map((first, second) -> first[0].getValue().toString() + ":" + second[0].getValue().toString(), first -> "second missing", second -> "first missing"))
				.contains("a:b");

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
