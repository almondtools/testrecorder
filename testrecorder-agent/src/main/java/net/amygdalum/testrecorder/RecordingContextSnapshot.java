package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.joining;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedOutput;

public class RecordingContextSnapshot implements ContextSnapshot {

    protected static final RecordingContextSnapshot INVALID = new RecordingContextSnapshot();

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


    private RecordingContextSnapshot() {
        this.valid = false;
        this.setupInput = new ArrayDeque<>();
        this.expectOutput = new ArrayDeque<>();
    }

    public RecordingContextSnapshot(long time, String key, MethodSignature signature) {
        this.time = time;
		this.key = key;
        this.signature = signature;
        this.valid = true;
        this.setupInput = new ArrayDeque<>();
        this.expectOutput = new ArrayDeque<>();
    }
    
    @Override
    public String getKey() {
		return key;
	}
    
	public boolean matches(String signature) {
		return key.equals(signature);
	}

    @Override
    public long getTime() {
        return time;
    }

    public void invalidate() {
        valid = false;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public Class<?> getDeclaringClass() {
        return signature.declaringClass;
    }

    @Override
    public Type getResultType() {
        return signature.resultType;
    }
    
    @Override
    public Annotation[] getResultAnnotation() {
        return signature.resultAnnotation;
    }

    @Override
    public String getMethodName() {
        return signature.methodName;
    }

    @Override
    public Type[] getArgumentTypes() {
        return signature.argumentTypes;
    }
    
    @Override
    public Annotation[][] getArgumentAnnotations() {
        return signature.argumentAnnotations;
    }

    @Override
    public Type getThisType() {
        if (setupThis != null) {
            return setupThis.getType();
        } else {
            return signature.declaringClass;
        }
    }

    @Override
    public SerializedValue getSetupThis() {
        return setupThis;
    }

    public void setSetupThis(SerializedValue setupThis) {
        this.setupThis = setupThis;
    }

    @Override
    public SerializedValue[] getSetupArgs() {
        return setupArgs;
    }

    public void setSetupArgs(SerializedValue... setupArgs) {
        this.setupArgs = setupArgs;
    }

    @Override
    public AnnotatedValue[] getAnnotatedSetupArgs() {
        AnnotatedValue[] annotatedValues = new AnnotatedValue[setupArgs.length];
        Annotation[][] annotations =  signature.argumentAnnotations;
        if (annotations.length != setupArgs.length) {
            annotations = new Annotation[setupArgs.length][];
            Arrays.fill(annotations, new Annotation[0]);
        }
        for (int i = 0; i < annotatedValues.length; i++) {
            annotatedValues[i] = new AnnotatedValue(annotations[i], setupArgs[i]);
        }
        return annotatedValues;
    }
    
    @Override
    public SerializedField[] getSetupGlobals() {
        return setupGlobals;
    }

    public void setSetupGlobals(SerializedField... setupGlobals) {
        this.setupGlobals = setupGlobals;
    }

    @Override
    public SerializedValue getExpectThis() {
        return expectThis;
    }

    public void setExpectThis(SerializedValue expectThis) {
        this.expectThis = expectThis;
    }

    @Override
    public SerializedValue getExpectResult() {
        return expectResult;
    }

    public void setExpectResult(SerializedValue expectResult) {
        this.expectResult = expectResult;
    }

    @Override
    public <T extends Annotation> Optional<T> getMethodAnnotation(Class<T> clazz) {
        for (int i = 0; i < signature.resultAnnotation.length; i++) {
            if (clazz.isInstance(signature.resultAnnotation[i])) {
                return Optional.of(clazz.cast(signature.resultAnnotation[i]));
            }
        }
        return Optional.empty();
    }
    
    @Override
    public SerializedValue getExpectException() {
        return expectException;
    }

    public void setExpectException(SerializedValue expectException) {
        this.expectException = expectException;
    }

    @Override
    public SerializedValue[] getExpectArgs() {
        return expectArgs;
    }

    @Override
    public AnnotatedValue[] getAnnotatedExpectArgs() {
        AnnotatedValue[] annotatedValues = new AnnotatedValue[expectArgs.length];
        Annotation[][] annotations =  signature.argumentAnnotations;
        if (annotations.length != expectArgs.length) {
            annotations = new Annotation[expectArgs.length][];
            Arrays.fill(annotations, new Annotation[0]);
        }
        for (int i = 0; i < annotatedValues.length; i++) {
            annotatedValues[i] = new AnnotatedValue(annotations[i], expectArgs[i]);
        }
        return annotatedValues;
    }
    
    public void setExpectArgs(SerializedValue... expectArgs) {
        this.expectArgs = expectArgs;
    }

    @Override
    public SerializedField[] getExpectGlobals() {
        return expectGlobals;
    }

    public void setExpectGlobals(SerializedField... expectGlobals) {
        this.expectGlobals = expectGlobals;
    }
    
    public void addInput(SerializedInput input) {
    	setupInput.add(input);
    }

    @Override
    public Queue<SerializedInput> getSetupInput() {
        return setupInput;
    }

	public Stream<SerializedInput> streamInput() {
		return setupInput.stream();
	}

	public boolean lastInputSatitisfies(Predicate<SerializedInput> predicate) {
		SerializedInput peek = setupInput.peekLast();
		return peek != null && predicate.test(peek);
	}

    public void addOutput(SerializedOutput output) {
    	expectOutput.add(output);
    }

    @Override
    public Queue<SerializedOutput> getExpectOutput() {
        return expectOutput;
    }

	public Stream<SerializedOutput> streamOutput() {
		return expectOutput.stream();
	}

	public boolean lastOutputSatitisfies(Predicate<SerializedOutput> predicate) {
		SerializedOutput peek = expectOutput.peekLast();
		return peek != null && predicate.test(peek);
	}

    @Override
    public String toString() {
        return signature.resultType.getTypeName() + " " + signature.methodName + Stream.of(signature.argumentTypes).map(type -> type.getTypeName()).collect(joining(",", "(", ")")) + " of " + signature.declaringClass.getName();
    }

}
