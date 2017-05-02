package net.amygdalum.testrecorder;

import static java.util.stream.Collectors.joining;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedOutput;

public class ContextSnapshot {

    protected static final ContextSnapshot INVALID = new ContextSnapshot();

    private long time;
    private Class<?> declaringClass;
    private Annotation[] resultAnnotation;
    private Type resultType;
    private String methodName;
    private Annotation[][] argumentAnnotations;
    private Type[] argumentTypes;

    private boolean valid;

    private SerializedValue setupThis;
    private SerializedValue[] setupArgs;
    private SerializedField[] setupGlobals;

    private SerializedValue expectThis;
    private SerializedValue expectResult;
    private SerializedValue expectException;
    private SerializedValue[] expectArgs;
    private SerializedField[] expectGlobals;

    private List<SerializedInput> setupInput;
    private List<SerializedOutput> expectOutput;


    private ContextSnapshot() {
        this.valid = false;
    }

    public ContextSnapshot(long time, Class<?> declaringClass, Annotation[] resultAnnotation,Type resultType, String methodName, Annotation[][] argumentAnnotations, Type[] argumentTypes) {
        this.time = time;
        this.declaringClass = declaringClass;
        this.resultAnnotation = resultAnnotation;
        this.resultType = resultType;
        this.methodName = methodName;
        this.argumentAnnotations = argumentAnnotations;
        this.argumentTypes = argumentTypes;
        this.valid = true;
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
        return declaringClass;
    }

    public Type getResultType() {
        return resultType;
    }
    
    public Annotation[] getResultAnnotation() {
        return resultAnnotation;
    }

    public String getMethodName() {
        return methodName;
    }

    public Type[] getArgumentTypes() {
        return argumentTypes;
    }
    
    public Annotation[][] getArgumentAnnotations() {
        return argumentAnnotations;
    }

    public Type getThisType() {
        if (setupThis != null) {
            return setupThis.getType();
        } else {
            return declaringClass;
        }
    }

    public SerializedValue getSetupThis() {
        return setupThis;
    }

    public void setSetupThis(SerializedValue setupThis) {
        this.setupThis = setupThis;
    }

    public SerializedValue[] getSetupArgs() {
        return setupArgs;
    }

    public void setSetupArgs(SerializedValue... setupArgs) {
        this.setupArgs = setupArgs;
    }

    public AnnotatedValue[] getAnnotatedSetupArgs() {
        AnnotatedValue[] annotatedValues = new AnnotatedValue[setupArgs.length];
        Annotation[][] annotations =  argumentAnnotations;
        if (annotations.length != setupArgs.length) {
            annotations = new Annotation[setupArgs.length][];
            Arrays.fill(annotations, new Annotation[0]);
        }
        for (int i = 0; i < annotatedValues.length; i++) {
            annotatedValues[i] = new AnnotatedValue(annotations[i], setupArgs[i]);
        }
        return annotatedValues;
    }
    
    public SerializedField[] getSetupGlobals() {
        return setupGlobals;
    }

    public void setSetupGlobals(SerializedField... setupGlobals) {
        this.setupGlobals = setupGlobals;
    }

    public SerializedValue getExpectThis() {
        return expectThis;
    }

    public void setExpectThis(SerializedValue expectThis) {
        this.expectThis = expectThis;
    }

    public SerializedValue getExpectResult() {
        return expectResult;
    }

    public void setExpectResult(SerializedValue expectResult) {
        this.expectResult = expectResult;
    }

    public <T extends Annotation> Optional<T> getMethodAnnotation(Class<T> clazz) {
        for (int i = 0; i < resultAnnotation.length; i++) {
            if (clazz.isInstance(resultAnnotation[i])) {
                return Optional.of(clazz.cast(resultAnnotation[i]));
            }
        }
        return Optional.empty();
    }
    
    public SerializedValue getExpectException() {
        return expectException;
    }

    public void setExpectException(SerializedValue expectException) {
        this.expectException = expectException;
    }

    public SerializedValue[] getExpectArgs() {
        return expectArgs;
    }

    public AnnotatedValue[] getAnnotatedExpectArgs() {
        AnnotatedValue[] annotatedValues = new AnnotatedValue[expectArgs.length];
        Annotation[][] annotations =  argumentAnnotations;
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

    public SerializedField[] getExpectGlobals() {
        return expectGlobals;
    }

    public void setExpectGlobals(SerializedField... expectGlobals) {
        this.expectGlobals = expectGlobals;
    }

    public List<SerializedInput> getSetupInput() {
        return setupInput;
    }

    public void setSetupInput(List<SerializedInput> setupInput) {
        this.setupInput = setupInput;
    }

    public List<SerializedOutput> getExpectOutput() {
        return expectOutput;
    }

    public void setExpectOutput(List<SerializedOutput> expectOutput) {
        this.expectOutput = expectOutput;
    }

    @Override
    public String toString() {
        return resultType.getTypeName() + " " + methodName + Stream.of(argumentTypes).map(type -> type.getTypeName()).collect(joining(",", "(", ")")) + " of " + declaringClass.getName();
    }

    public static class AnnotatedValue {
        public Annotation[] annotations;
        public SerializedValue value;
        
        public AnnotatedValue(Annotation[] annotations, SerializedValue value) {
            this.annotations = annotations;
            this.value = value;
        }
        
        public <T extends Annotation> Optional<T> getAnnotation(Class<T> clazz) {
            for (int i = 0; i < annotations.length; i++) {
                if (clazz.isInstance(annotations[i])) {
                    return Optional.of(clazz.cast(annotations[i]));
                }
            }
            return Optional.empty();
        }
        
    }

}
