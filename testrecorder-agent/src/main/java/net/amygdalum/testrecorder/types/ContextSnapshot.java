package net.amygdalum.testrecorder.types;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.util.BiOptional;

public class ContextSnapshot implements Serializable {

	public static final ContextSnapshot INVALID = new ContextSnapshot();

	private long time;
	private String key;
	private VirtualMethodSignature signature;

	private boolean valid;

	private SerializedValue setupThis;
	private SerializedArgument[] setupArgs;
	private SerializedField[] setupGlobals;

	private SerializedValue expectThis;
	private SerializedResult expectResult;
	private SerializedValue expectException;
	private SerializedArgument[] expectArgs;
	private SerializedField[] expectGlobals;

	private Deque<SerializedInput> setupInput;
	private Deque<SerializedOutput> expectOutput;

	private ContextSnapshot() {
		this.valid = false;
		this.setupInput = new ArrayDeque<>();
		this.expectOutput = new ArrayDeque<>();
	}

	public ContextSnapshot(long time, String key, VirtualMethodSignature signature) {
		this.time = time;
		this.key = key;
		this.signature = signature;
		this.valid = true;
		this.setupInput = new ArrayDeque<>();
		this.expectOutput = new ArrayDeque<>();
	}

	public ClassLoader getClassLoader() {
		return signature.getClassLoader();
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
		return signature.signature.declaringClass;
	}

	public Type getResultType() {
		return signature.signature.resultType;
	}

	public String getMethodName() {
		return signature.signature.methodName;
	}

	public Type[] getArgumentTypes() {
		return signature.signature.argumentTypes;
	}

	public Type getThisType() {
		if (setupThis != null) {
			return setupThis.getType();
		} else {
			return signature.signature.declaringClass;
		}
	}

	public Class<?>[] getActualArgumentTypes() {
		return Arrays.stream(setupArgs)
			.map(arg -> arg.getValue() == null ? Object.class : arg.getValue().getType())
			.toArray(Class[]::new);
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

	public SerializedArgument[] getSetupArgs() {
		return setupArgs;
	}

	public Optional<SerializedArgument> onSetupArg(int index) {
		if (setupArgs.length <= index) {
			return Optional.empty();
		}
		return Optional.ofNullable(setupArgs[index]);
	}

	public Stream<SerializedArgument> streamSetupArgs() {
		return Arrays.stream(setupArgs);
	}

	public void setSetupArgs(SerializedValue... setupArgs) {
		this.setupArgs = argumentsOf(setupArgs);
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

	public SerializedResult getExpectResult() {
		return expectResult;
	}

	public Optional<SerializedResult> onExpectResult() {
		return Optional.ofNullable(expectResult);
	}

	public Stream<SerializedResult> streamExpectResult() {
		return Stream.of(expectResult)
			.filter(Objects::nonNull);
	}

	public void setExpectResult(SerializedValue expectResult) {
		this.expectResult = resultOf(expectResult);
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

	public SerializedArgument[] getExpectArgs() {
		return expectArgs;
	}

	public Stream<SerializedArgument> streamExpectArgs() {
		return Arrays.stream(expectArgs);
	}

	public Optional<SerializedArgument> onExpectArg(int index) {
		if (expectArgs.length <= index) {
			return Optional.empty();
		}
		return Optional.ofNullable(expectArgs[index]);
	}

	public void setExpectArgs(SerializedValue... expectArgs) {
		this.expectArgs = argumentsOf(expectArgs);
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

	public BiOptional<SerializedArgument[]> onArgs() {
		return BiOptional.ofNullable(setupArgs, expectArgs);
	}

	public BiOptional<SerializedField[]> onGlobals() {
		return BiOptional.ofNullable(setupGlobals, expectGlobals);
	}

	public String toString() {
		return signature.toString();
	}

	private SerializedArgument[] argumentsOf(SerializedValue[] argumentValues) {
		SerializedArgument[] arguments = new SerializedArgument[argumentValues.length];
		for (int i = 0; i < arguments.length; i++) {
			arguments[i] = new SerializedArgument(i, signature.signature, argumentValues[i]);
		}
		return arguments;
	}

	private SerializedResult resultOf(SerializedValue result) {
		return new SerializedResult(signature.signature, result);
	}

}
