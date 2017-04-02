package net.amygdalum.testrecorder.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating a type field, method result or param with this hint will instruct the deserializer
 * - to keep the construction of the annotated entity in a factory method (default would be inline in the test)
 * 
 * This is a future feature.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface PreferFactoryMethods {
}
