package net.amygdalum.testrecorder.types;

import static java.util.stream.Collectors.joining;
import static net.amygdalum.testrecorder.util.Types.baseType;
import static net.amygdalum.testrecorder.util.Types.getDeclaredMethod;
import static net.amygdalum.testrecorder.util.Types.serializableOf;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Stream;

public class MethodSignature implements Serializable {

	public Class<?> declaringClass;
	public Type resultType;
	public String methodName;
	public Type[] argumentTypes;

	public MethodSignature(Class<?> declaringClass, Type resultType, String methodName, Type[] argumentTypes) {
		assert declaringClass != null;
		assert methodName != null;
		assert resultType != null;
		assert argumentTypes != null;
		this.declaringClass = declaringClass;
		this.resultType = serializableOf(resultType);
		this.methodName = methodName;
		this.argumentTypes = serializableOf(argumentTypes);
	}

	public ClassLoader getClassLoader() {
		return declaringClass.getClassLoader();
	}

	public Method resolveMethod() throws NoSuchMethodException {
		return getDeclaredMethod(declaringClass, methodName, getParameterTypes());
	}

	private Class<?>[] getParameterTypes() {
		return Arrays.stream(argumentTypes)
			.map(type -> baseType(type))
			.toArray(Class[]::new);
	}

	@Override
	public int hashCode() {
		return declaringClass.hashCode() * 37
			+ methodName.hashCode() * 29
			+ resultType.hashCode() * 17
			+ Arrays.hashCode(argumentTypes) * 11;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MethodSignature that = (MethodSignature) obj;
		return this.declaringClass.equals(that.declaringClass)
			&& this.methodName.equals(that.methodName)
			&& this.resultType.equals(that.resultType)
			&& Arrays.equals(this.argumentTypes, that.argumentTypes);
	}

	@Override
	public String toString() {
		return resultType.getTypeName() + " " + methodName + Stream.of(argumentTypes).map(type -> type.getTypeName()).collect(joining(",", "(", ")")) + " of " + declaringClass.getName();
	}

}
