package net.amygdalum.testrecorder.values;

import static net.amygdalum.testrecorder.asm.ByteCode.argumentTypesFrom;
import static net.amygdalum.testrecorder.asm.ByteCode.classFrom;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandleInfo;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class LambdaSignature {

	private String capturingClass;

	private String instantiatedMethodType;

	private String functionalInterfaceClass;
	private String functionalInterfaceMethodName;
	private String functionalInterfaceMethodSignature;

	private String implClass;
	private int implMethodKind;
	private String implMethodName;
	private String implMethodSignature;
	
	public LambdaSignature() {
	}

	public LambdaSignature withCapturingClass(String capturingClass) {
		this.capturingClass = capturingClass;
		return this;
	}

	public LambdaSignature withInstantiatedMethodType(String instantiatedMethodType) {
		this.instantiatedMethodType = instantiatedMethodType;
		return this;
	}

	public LambdaSignature withFunctionalInterface(String functionalInterfaceClass, String functionalInterfaceMethodName, String functionalInterfaceMethodSignature) {
		this.functionalInterfaceClass = functionalInterfaceClass;
		this.functionalInterfaceMethodName = functionalInterfaceMethodName;
		this.functionalInterfaceMethodSignature = functionalInterfaceMethodSignature;
		return this;
	}

	public LambdaSignature withImplMethod(String implClass, int implMethodKind, String implMethodName, String implMethodSignature) {
		this.implClass = implClass;
		this.implMethodKind = implMethodKind;
		this.implMethodName = implMethodName;
		this.implMethodSignature = implMethodSignature;
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T deserialize(Class<T> resultType, Object... capturedArgs) {
		try {
			Class<?> capturingClass = classFrom(this.capturingClass);
			ClassLoader cl = capturingClass.getClassLoader();
			Class<?> implClass = classFrom(this.implClass, cl);
			Class<?> interfaceType = classFrom(this.functionalInterfaceClass, cl);

			Lookup lookup = privateLookup(implClass);

			MethodType implMethodType = MethodType.fromMethodDescriptorString(implMethodSignature, cl);
			MethodType interfaceMethodType = MethodType.fromMethodDescriptorString(functionalInterfaceMethodSignature, null);

			MethodHandle implMethod = implMethod(lookup, implClass, implMethodType);

			MethodType factoryMethodType = factoryType(interfaceType, interfaceMethodType, implClass, implMethodType);

			MethodType instantiatedMethodType = instantiatedType(interfaceMethodType, implMethodType);

			CallSite callSite = LambdaMetafactory.altMetafactory(lookup, functionalInterfaceMethodName, factoryMethodType, interfaceMethodType, implMethod, instantiatedMethodType, 1);

			return (T) callSite.dynamicInvoker().invokeWithArguments(capturedArgs);
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private MethodType factoryType(Class<?> interfaceType, MethodType interfaceMethodType, Class<?> implClass, MethodType implMethodType) {
		MethodType factoryType = MethodType.methodType(interfaceType, Arrays.copyOf(implMethodType.parameterArray(), implMethodType.parameterCount() - interfaceMethodType.parameterCount()));
		if (isInstanceMethod()) {
			return factoryType.insertParameterTypes(0, implClass);
		}
		return factoryType;
	}

	private MethodType instantiatedType(MethodType interfaceMethodType, MethodType implType) {
		if (implType.parameterCount() > interfaceMethodType.parameterCount()) {
			return implType.dropParameterTypes(0, implType.parameterCount() - interfaceMethodType.parameterCount());
		} else {
			return implType;
		}
	}

	private boolean isInstanceMethod() {
		return implMethodKind != MethodHandleInfo.REF_invokeStatic;
	}

	private MethodHandle implMethod(Lookup lookup, Class<?> implClass, MethodType implType) throws NoSuchMethodException, IllegalAccessException {
		switch (implMethodKind) {
		case MethodHandleInfo.REF_invokeInterface:
		case MethodHandleInfo.REF_invokeVirtual:
			return lookup.findVirtual(implClass, implMethodName, implType);
		case MethodHandleInfo.REF_invokeSpecial:
			return lookup.findSpecial(implClass, implMethodName, implType, implClass);
		case MethodHandleInfo.REF_invokeStatic:
			return lookup.findStatic(implClass, implMethodName, implType);
		default:
			throw new RuntimeException("Unsupported impl method kind " + implMethodKind);
		}
	}

	private Lookup privateLookup(Class<?> clazz) throws ReflectiveOperationException {
		Constructor<Lookup> constructor = Lookup.class.getDeclaredConstructor(Class.class, Integer.TYPE);
		constructor.setAccessible(true);
		return constructor.newInstance(clazz, 15);
	}

	public String getCapturingClass() {
		return capturingClass;
	}

	public String getInstantiatedMethodType() {
		return instantiatedMethodType;
	}

	public String getFunctionalInterfaceClass() {
		return functionalInterfaceClass;
	}

	public String getFunctionalInterfaceMethodName() {
		return functionalInterfaceMethodName;
	}

	public String getFunctionalInterfaceMethodSignature() {
		return functionalInterfaceMethodSignature;
	}

	public Method getFunctionalInterfaceMethod() {
		try {
			Class<?> base = classFrom(functionalInterfaceClass);
			Class<?>[] parameterTypes = argumentTypesFrom(functionalInterfaceMethodSignature);
			return base.getDeclaredMethod(functionalInterfaceMethodName, parameterTypes);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public String getImplClass() {
		return implClass;
	}

	public int getImplMethodKind() {
		return implMethodKind;
	}

	public String getImplMethodName() {
		return implMethodName;
	}

	public String getImplMethodSignature() {
		return implMethodSignature;
	}

	public Method getImplMethod() {
		try {
			Class<?> base = classFrom(implClass);
			Class<?>[] parameterTypes = argumentTypesFrom(implMethodSignature);
			return base.getDeclaredMethod(implMethodName, parameterTypes);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

}
