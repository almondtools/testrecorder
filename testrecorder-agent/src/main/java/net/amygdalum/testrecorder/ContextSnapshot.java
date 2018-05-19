package net.amygdalum.testrecorder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.Queue;

import net.amygdalum.testrecorder.types.SerializedValue;
import net.amygdalum.testrecorder.values.SerializedField;
import net.amygdalum.testrecorder.values.SerializedInput;
import net.amygdalum.testrecorder.values.SerializedOutput;

public interface ContextSnapshot {

	String getKey();

	long getTime();

	boolean isValid();

	Class<?> getDeclaringClass();

	Type getResultType();

	Annotation[] getResultAnnotation();

	String getMethodName();

	Type[] getArgumentTypes();

	Annotation[][] getArgumentAnnotations();

	Type getThisType();

	SerializedValue getSetupThis();

	SerializedValue[] getSetupArgs();

	SerializedField[] getSetupGlobals();

	SerializedValue getExpectThis();

	SerializedValue getExpectResult();

	<T extends Annotation> Optional<T> getMethodAnnotation(Class<T> clazz);

	SerializedValue getExpectException();

	SerializedValue[] getExpectArgs();

	SerializedField[] getExpectGlobals();

	Queue<SerializedInput> getSetupInput();

	Queue<SerializedOutput> getExpectOutput();

	AnnotatedValue[] getAnnotatedSetupArgs();

	AnnotatedValue[] getAnnotatedExpectArgs();

}
