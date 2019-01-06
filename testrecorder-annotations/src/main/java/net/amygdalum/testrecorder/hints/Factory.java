package net.amygdalum.testrecorder.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is a hint for the SetupGenerator:
 * - to use the given static factory method for object construction
 * 
 * The conventions of a Factory are:
 * - each non-default-field has a corresponding parameter in the factory method
 * - each default field may or may not be part of the factory method
 * - if multiple methods match the specification (class and method name) then the shortest matching is selected 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface Factory {
	Class<?> clazz();
	String method();
}
