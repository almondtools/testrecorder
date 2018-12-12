package net.amygdalum.testrecorder.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is a hint for the SetupGenerator:
 * - to use the given builder class for object construction
 * 
 * The conventions of a Builder are:
 * - each field has a corresponding with-method (setting the Field, returning the Builder)
 * - there exists a constructor (without arguments)
 * - there exists a build method (without arguments, returning the final object)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface Builder {
	Class<?> builder();
}
