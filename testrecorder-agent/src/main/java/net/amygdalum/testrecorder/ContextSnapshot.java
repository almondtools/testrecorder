package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.joining;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.util.BiOptional;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedOutput;

public class ContextSnapshot implements Serializable {

	protected static final ContextSnapshot INVALID = new ContextSnapshot();

	private long time;
	private String key;
	private MethodSignature signature;

	private boolean valid;

	private SerializedValue setupThis;
	private SerializedValue[] setupArgs;
	private SerializedField[] setupGlobals;

	private SerializedValue expectThis;
	private SerializedValue expectResult;
	private SerializedValue expectException;
	private SerializedValue[] expectArgs;
	private SerializedField[] expectGlobals;

	private Deque<SerializedInput> setupInput;
	private Deque<SerializedOutput> expectOutput;

	private ContextSnapshot() {
		this.valid = false;
		this.setupInput = new ArrayDeque<>();
		this.expectOutput = new ArrayDeque<>();
	}

	public ContextSnapshot(long time, String key, MethodSignature signature) {
		this.time = time;
		this.key = key;
		this.signature = signature;
		this.valid = true;
		this.setupInput = new ArrayDeque<>();
		this.expectOutput = new ArrayDeque<>();
	}

	public String getKey() {
		return key;
	}

	public boolean matches(String signature) {
		return key.equals(signature);
	}

	public long getTime() {
		return time;
	}

	public void invalidate() {
		valid = false;
	}

	public boolean isValid() {
		return valid;
	}

	public Class<?> getDeclaringClass() {
		return signature.declaringClass;
	}

	public Type getResultType() {
		return signature.resultType;
	}

	public Annotation[] getResultAnnotation() {
		return signature.resultAnnotation;
	}

	public String getMethodName() {
		return signature.methodName;
	}

	public Type[] getArgumentTypes() {
		return signature.argumentTypes;
	}

	public Annotation[][] getArgumentAnnotations() {
		return signature.argumentAnnotations;
	}

	public Type getThisType() {
		if (setupThis != null) {
			return setupThis.getType();
		} else {
			return signature.declaringClass;
		}
	}

	public SerializedValue getSetupThis() {
		return setupThis;
	}

	public Optional<SerializedValue> onSetupThis() {
		return Optional.ofNullable(setupThis);
	}

	public Stream<SerializedValue> streamSetupThis() {
		return Stream.of(setupThis)
			.filter(Objects::nonNull);
	}

	public void setSetupThis(SerializedValue setupThis) {
		this.setupThis = setupThis;
	}

	public SerializedValue[] getSetupArgs() {
		return setupArgs;
	}

	public Stream<SerializedValue> streamSetupArgs() {
		return Arrays.stream(setupArgs);
	}

	public void setSetupArgs(SerializedValue... setupArgs) {
		this.setupArgs = setupArgs;
	}

	public AnnotatedValue[] getAnnotatedSetupArgs() {
		return streamAnnotatedSetupArgs().toArray(AnnotatedValue[]::new);
	}

	public Stream<AnnotatedValue> streamAnnotatedSetupArgs() {
		Annotation[][] annotations = align(signature.argumentAnnotations, setupArgs);
		Type[] argumentTypes = signature.argumentTypes;
		return IntStream.range(0, setupArgs.length)
			.mapToObj(i -> new AnnotatedValue(argumentTypes[i], annotations[i], setupArgs[i]));
	}

	public SerializedField[] getSetupGlobals() {
		return setupGlobals;
	}

	public Stream<SerializedField> streamSetupGlobals() {
		return Arrays.stream(setupGlobals);
	}

	public void setSetupGlobals(SerializedField... setupGlobals) {
		this.setupGlobals = setupGlobals;
	}

	public SerializedValue getExpectThis() {
		return expectThis;
	}

	public Optional<SerializedValue> onExpectThis() {
		return Optional.ofNullable(expectThis);
	}

	public Stream<SerializedValue> streamExpectThis() {
		return Stream.of(expectThis)
			.filter(Objects::nonNull);
	}

	public void setExpectThis(SerializedValue expectThis) {
		this.expectThis = expectThis;
	}

	public SerializedValue getExpectResult() {
		return expectResult;
	}

	public Optional<SerializedValue> onExpectResult() {
		return Optional.ofNullable(expectResult);
	}

	public Stream<SerializedValue> streamExpectResult() {
		return Stream.of(expectResult)
			.filter(Objects::nonNull);
	}

	public void setExpectResult(SerializedValue expectResult) {
		this.expectResult = expectResult;
	}

	public <T extends Annotation> Optional<T> getMethodAnnotation(Class<T> clazz) {
		for (int i = 0; i < signature.resultAnnotation.length; i++) {
			if (clazz.isInstance(signature.resultAnnotation[i])) {
				return Optional.of(clazz.cast(signature.resultAnnotation[i]));
			}
		}
		return Optional.empty();
	}

	public SerializedValue getExpectException() {
		return expectException;
	}

	public Optional<SerializedValue> onExpectException() {
		return Optional.ofNullable(expectException);
	}

	public Stream<SerializedValue> streamExpectException() {
		return Stream.of(expectException)
			.filter(Objects::nonNull);
	}

	public void setExpectException(SerializedValue expectException) {
		this.expectException = expectException;
	}

	public SerializedValue[] getExpectArgs() {
		return expectArgs;
	}

	public Stream<SerializedValue> streamExpectArgs() {
		return Arrays.stream(expectArgs);
	}

	public AnnotatedValue[] getAnnotatedExpectArgs() {
		return streamAnnotatedExpectArgs().toArray(AnnotatedValue[]::new);
	}

	public Stream<AnnotatedValue> streamAnnotatedExpectArgs() {
		Annotation[][] annotations = align(signature.argumentAnnotations, expectArgs);
		Type[] argumentTypes = signature.argumentTypes;
		return IntStream.range(0, expectArgs.length)
			.mapToObj(i -> new AnnotatedValue(argumentTypes[i], annotations[i], expectArgs[i]));
	}

	public void setExpectArgs(SerializedValue... expectArgs) {
		this.expectArgs = expectArgs;
	}

	public SerializedField[] getExpectGlobals() {
		return expectGlobals;
	}

	public Stream<SerializedField> streamExpectGlobals() {
		return Arrays.stream(expectGlobals);
	}

	public void setExpectGlobals(SerializedField... expectGlobals) {
		this.expectGlobals = expectGlobals;
	}

	public void addInput(SerializedInput input) {
		setupInput.add(input);
	}

	public Queue<SerializedInput> getSetupInput() {
		return setupInput;
	}

	public Stream<SerializedInput> streamInput() {
		return setupInput.stream();
	}

	public boolean hasSetupInput() {
		return !setupInput.isEmpty();
	}

	public boolean lastInputSatitisfies(Predicate<SerializedInput> predicate) {
		SerializedInput peek = setupInput.peekLast();
		return peek != null
			&& predicate.test(peek);
	}

	public void addOutput(SerializedOutput output) {
		expectOutput.add(output);
	}

	public Queue<SerializedOutput> getExpectOutput() {
		return expectOutput;
	}

	public Stream<SerializedOutput> streamOutput() {
		return expectOutput.stream();
	}

	public boolean hasExpectOutput() {
		return !expectOutput.isEmpty();
	}

	public boolean lastOutputSatitisfies(Predicate<SerializedOutput> predicate) {
		SerializedOutput peek = expectOutput.peekLast();
		return peek != null
			&& predicate.test(peek);
	}

	public BiOptional<SerializedValue> onThis() {
		return BiOptional.ofNullable(setupThis, expectThis);
	}

	private Annotation[][] align(Annotation[][] annotations, SerializedValue[] values) {
		if (annotations.length != values.length) {
			Annotation[][] resultannotations = new Annotation[values.length][];
			Arrays.fill(resultannotations, new Annotation[0]);
			return resultannotations;
		}
		return annotations;
	}

	public String toString() {
		return signature.resultType.getTypeName() + " " + signature.methodName + Stream.of(signature.argumentTypes).map(type -> type.getTypeName()).collect(joining(",", "(", ")")) + " of "
			+ signature.declaringClass.getName();
	}

}
