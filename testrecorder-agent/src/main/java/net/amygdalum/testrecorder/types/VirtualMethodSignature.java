package net.amygdalum.testrecorder.types;

import static net.amygdalum.testrecorder.asm.ByteCode.argumentTypesFrom;
import static net.amygdalum.testrecorder.asm.ByteCode.classFrom;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import net.amygdalum.testrecorder.util.Types;

public class VirtualMethodSignature implements Serializable {

	public static final VirtualMethodSignature NULL = new VirtualMethodSignature() {
		@Override
		public synchronized boolean validIn(Class<?> clazz) {
			return false;
		}

	};

	public MethodSignature signature;

	private Set<Class<?>> valid;
	private Set<Class<?>> invalid;

	private VirtualMethodSignature() {
		this.valid = new HashSet<>();
		this.invalid = new HashSet<>();
	}

	public VirtualMethodSignature(MethodSignature signature) {
		this.signature = signature;

		this.valid = new HashSet<>();
		this.invalid = new HashSet<>();
	}

	public static VirtualMethodSignature fromDescriptor(String className, String methodName, String methodDesc) {
		try {
			Class<?> clazz = classFrom(className);
			Method method = Types.getDeclaredMethod(clazz, methodName, argumentTypesFrom(methodDesc));

			return fromDescriptor(clazz, method);
		} catch (RuntimeException | ReflectiveOperationException e) {
			throw new SerializationException(e);
		}

	}

	public static VirtualMethodSignature fromDescriptor(Method method) {
		return fromDescriptor(method.getDeclaringClass(), method);
	}

	public static VirtualMethodSignature fromDescriptor(Class<?> clazz, Method method) {
		MethodSignature signature = new MethodSignature(clazz, method.getGenericReturnType(), method.getName(), method.getGenericParameterTypes());
		return new VirtualMethodSignature(signature);
	}

	public synchronized boolean validIn(Class<?> clazz) {
		if (valid.contains(clazz)) {
			return true;
		} else if (invalid.contains(clazz)) {
			return false;
		}
		try {
			Class<?> resolvedClass = resolveClass(clazz);
	
			boolean valid = Objects.equals(resolvedClass.getName(), signature.declaringClass.getName());
			if (valid) {
				this.valid.add(clazz);
			} else {
				this.invalid.add(clazz);
			}
			return valid;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	private Class<?> resolveClass(Class<?> clazz) throws NoSuchMethodException {
		ClassLoader loader = clazz.getClassLoader();
		Class<?>[] parameterTypes = Arrays.stream(signature.argumentTypes)
			.map(type -> {
				try {
					return Types.classFrom(Types.baseType(type), loader);
				} catch (ClassNotFoundException e) {
					return null;
				}
			})
			.toArray(Class[]::new);
		Method method = Types.getDeclaredMethod(clazz, signature.methodName, parameterTypes);
		return method.getDeclaringClass();
	}
	
	@Override
	public String toString() {
		return signature.toString();
	}

}
