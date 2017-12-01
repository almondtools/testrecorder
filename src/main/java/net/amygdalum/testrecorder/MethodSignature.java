package net.amygdalum.testrecorder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import net.amygdalum.testrecorder.asm.ByteCode;
import net.amygdalum.testrecorder.types.SerializationException;
import net.amygdalum.testrecorder.util.Types;

public class MethodSignature {

	public Class<?> declaringClass;
	public Annotation[] resultAnnotation;
	public Type resultType;
	public String methodName;
	public Annotation[][] argumentAnnotations;
	public Type[] argumentTypes;

	public MethodSignature(Class<?> declaringClass, Annotation[] resultAnnotation, Type resultType, String methodName, Annotation[][] argumentAnnotations, Type[] argumentTypes) {
		this.declaringClass = declaringClass;
		this.resultAnnotation = resultAnnotation;
		this.resultType = resultType;
		this.methodName = methodName;
		this.argumentAnnotations = argumentAnnotations;
		this.argumentTypes = argumentTypes;
	}

	private MethodSignature() {
	}

	public static MethodSignature fromDescriptor(String className, String methodName, String methodDesc) {
		try {
			MethodSignature signature = new MethodSignature();
			signature.declaringClass = ByteCode.classFromInternalName(className);
			Method method = Types.getDeclaredMethod(signature.declaringClass, methodName, ByteCode.getArgumentTypes(methodDesc));
			signature.resultAnnotation = method.getAnnotations();
			signature.resultType = method.getGenericReturnType();
			signature.methodName = method.getName();
			signature.argumentAnnotations = method.getParameterAnnotations();
			signature.argumentTypes = method.getGenericParameterTypes();
			return signature;
		} catch (RuntimeException | ReflectiveOperationException e) {
			throw new SerializationException(e);
		}

	}

}
