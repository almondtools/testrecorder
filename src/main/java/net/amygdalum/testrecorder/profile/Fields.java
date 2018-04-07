package net.amygdalum.testrecorder.profile;

import java.lang.reflect.Field;

import org.objectweb.asm.Type;

/**
 * used to specify a field or multiple fields. Provides two predicate methods for matching at compile time and at run time.
 */
public interface Fields {

	/**
	 * defines matching with runtime (reflection) fields.
	 * 
	 * @param field a field specified via reflection
	 * @return true if field is covered by this predicate, false otherwise
	 */
	boolean matches(Field field);

	/**
	 * defines matching with compile time field specifications.
	 * 
     * @param className the internal name of the class (e.g. java/lang/String for java.lang.String)
	 * @param fieldName the name of the field (e.g chars)
	 * @param fieldDescriptor the type descriptor of the field (e.g. [C; for char[])
	 * @return true if the compile time description of the field is covered by this predicate, false otherwise
	 */
	boolean matches(String className, String fieldName, String fieldDescriptor);

	/**
	 * specifies a set of fields by name
	 * 
	 * @param name the name of the field (it may be qualified with the declaring class, but signature spec is not accepted)
	 * @return a predicate return true for every field of the given name
	 */
	static Fields byName(String name) {
		return new FieldsByName(name);
	}

	/**
	 * specifies a field by description
	 * 
     * @param className the internal name of the class (e.g. java/lang/String for java.lang.String)
	 * @param fieldName the name of the field (e.g chars)
	 * @param fieldDescriptor the type descriptor of the field (e.g. [C; for char[])
	 * @return a predicate return true for the specified field
	 */
	static Fields byDescription(String className, String fieldName, String fieldDescriptor) {
		return new FieldDescription(className, fieldName, fieldDescriptor);
	}

	/**
	 * specifies a field by sample field object
	 * 
	 * @param field the field to be described
	 * @return a predicate return true for the specified field
	 */
	static Fields byDescription(Field field) {
		String className = Type.getInternalName(field.getDeclaringClass());
		String fieldName = field.getName();
		String fieldDescriptor = Type.getDescriptor(field.getType());
		return new FieldDescription(className, fieldName, fieldDescriptor);
	}
	
}
