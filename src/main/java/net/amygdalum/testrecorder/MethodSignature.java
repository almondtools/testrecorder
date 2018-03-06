package net.amygdalum.testrecorder;

import static net.amygdalum.testrecorder.asm.ByteCode.argumentTypesFrom;
import static net.amygdalum.testrecorder.asm.ByteCode.classFrom;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import net.amygdalum.testrecorder.types.SerializationException;
import net.amygdalum.testrecorder.util.Types;

public class MethodSignature {

	public static final MethodSignature NULL = new MethodSignature() {
		@Override
		public synchronized boolean validIn(Class<?> clazz) {
			return false;
		}

	};

	public Class<?> declaringClass;
	public Annotation[] resultAnnotation;
	public Type resultType;
	public String methodName;
	public Annotation[][] argumentAnnotations;
	public Type[] argumentTypes;

	private Set<Class<?>> valid;
	private Set<Class<?>> invalid;

	private MethodSignature() {
		this.valid = new HashSet<>();
		this.invalid = new HashSet<>();
	}

	public MethodSignature(Class<?> declaringClass, Annotation[] resultAnnotation, Type resultType, String methodName, Annotation[][] argumentAnnotations, Type[] argumentTypes) {
		this.declaringClass = declaringClass;
		this.resultAnnotation = resultAnnotation;
		this.resultType = resultType;
		this.methodName = methodName;
		this.argumentAnnotations = argumentAnnotations;
		this.argumentTypes = argumentTypes;

		this.valid = new HashSet<>();
		this.invalid = new HashSet<>();
	}

	public static MethodSignature fromDescriptor(String className, String methodName, String methodDesc) {
		try {
			Class<?> clazz = classFrom(className);
			Method method = Types.getDeclaredMethod(clazz, methodName, argumentTypesFrom(methodDesc));

			return new MethodSignature(clazz, method.getAnnotations(), method.getGenericReturnType(), method.getName(), method.getParameterAnnotations(), method.getGenericParameterTypes());
		} catch (RuntimeException | ReflectiveOperationException e) {
			throw new SerializationException(e);
		}

	}

	public synchronized boolean validIn(Class<?> clazz) {
		if (valid.contains(clazz)) {
			return true;
		} else if (invalid.contains(clazz)) {
			return false;
		}
		Class<?> resolvedClass = resolveClass(clazz);

		boolean valid = Objects.equals(resolvedClass.getName(), declaringClass.getName());
		if (valid) {
			this.valid.add(clazz);
		} else {
			this.invalid.add(clazz);
		}
		return valid;
	}

	private Class<?> resolveClass(Class<?> clazz) {
		try {
			ClassLoader loader = clazz.getClassLoader();
			Class<?>[] parameterTypes = Arrays.stream(argumentTypes)
				.map(type -> {
					try {
						return Types.classFrom(Types.baseType(type), loader);
					} catch (ClassNotFoundException e) {
						return null;
					}
				})
				.toArray(Class[]::new);
			Method method = Types.getDeclaredMethod(clazz, methodName, parameterTypes);
			return method.getDeclaringClass();
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

}
