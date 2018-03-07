package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Method;

import org.objectweb.asm.Type;

/**
 * used to specify a method or multiple methods. Provides two predicate methods for matching at compile time and at run time.
 */
public interface Methods {

	/**
	 * defines matching with runtime (reflection) methods.
	 * 
	 * @param method a method specified via reflection
	 * @return true if method is covered by this predicate, false otherwise
	 */
	boolean matches(Method method);

	/**
	 * defines matching with compile time method specifications.
	 * 
	 * @param className the internal name of the class (e.g. java/lang/String for java.lang.String)
	 * @param methodName the name of the method (e.g getBytes)
	 * @param methodDescriptor the method descriptor of the method (e.g. (Ljava/nio/Charset;)[B; for byte[] getBytes(Charset charset))
	 * @return true if the compile time description of the method is covered by this predicate, false otherwise
	 */
	boolean matches(String className, String methodName, String methodDescriptor);

	/**
	 * specifies a set of methods by name
	 * 
	 * @param name the name of the method (it may be qualified with the declaring class, but signature spec is not accepted)
	 * @return a predicate return true for every method of the given name
	 */
	static Methods byName(String name) {
		return new MethodsByName(name);
	}

	/**
	 * specifies a method by description
	 * 
	 * @param className the internal name of the class (e.g. java/lang/String for java.lang.String)
	 * @param methodName the name of the method (e.g getBytes)
	 * @param methodDescriptor the method descriptor of the method (e.g. (Ljava/nio/Charset;)[B; for byte[] getBytes(Charset charset))
	 * @return a predicate return true for the specified method
	 */
	static Methods byDescription(String className, String methodName, String methodDescriptor) {
		return new MethodDescription(className, methodName, methodDescriptor);
	}

	/**
	 * specifies a method by an sample method object
	 * 
	 * @param method the method to be described
	 * @return a predicate return true for the specified method
	 */
	static Methods byDescription(Method method) {
		String className = Type.getInternalName(method.getDeclaringClass());
		String methodName = method.getName();
		String methodDescriptor = Type.getMethodDescriptor(method);
		return new MethodDescription(className, methodName, methodDescriptor);
	}

}
