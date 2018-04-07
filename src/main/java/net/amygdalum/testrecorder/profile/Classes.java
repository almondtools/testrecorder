package net.amygdalum.testrecorder.profile;

import org.objectweb.asm.Type;

/**
 * used to specify a class or multiple classes. Provides two predicate methods for matching at compile time and at run time.
 */
public interface Classes {

	/**
	 * defines matching with runtime (reflection) classes.
	 * 
	 * @param type a class specified via reflection or {@link Object#getClass()}
	 * @return true if class is covered by this predicate, false otherwise
	 */
	boolean matches(Class<?> type);

	/**
	 * defines matching with compile time class specifications.
	 * 
	 * @param className the internal name of the specified class (e.g. java/lang/String for java.lang.String)
	 * @return true if class is covered by this predicate, false otherwise
	 */
	boolean matches(String className);

	/**
	 * specifies a class by name (common name in this case, i.e. java.lang.String for java.lang.String)
	 * 
	 * @param name the name of the class (may be abbreviated to the simple name)
	 * @return a predicate return true for the class of the given name
	 */
	static Classes byName(String name) {
		return new ClassesByName(name);
	}

	/**
	 * specifies a set of classes by package name (common name in this case, i.e. java.lang for java.lang)
	 * 
	 * @param name the package containing the specified classes
	 * @return a predicate return true for every class in the given package
	 */
	static Classes byPackage(String name) {
		return new ClassesByPackage(name);
	}

	/**
	 * specifies a class by internal name (i.e. java/lang/String for java.lang.String)
	 * 
	 * @param className the internal name of the class
	 * @return a predicate return true for the class of the given name
	 */
	static Classes byDescription(String className) {
		return new ClassDescription(className);
	}

	/**
	 * specifies a class by a sample class object
	 * 
	 * @param clazz the class to be described
	 * @return a predicate return true for the class of the given name
	 */
	static Classes byDescription(Class<?> clazz) {
		String className = Type.getInternalName(clazz);
		return new ClassDescription(className);
	}
	
}
