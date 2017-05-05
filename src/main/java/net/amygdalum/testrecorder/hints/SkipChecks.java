package net.amygdalum.testrecorder.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating a type field, method result or param with this hint will instruct the deserializer
 * - to skip the generation of a matcher for this entity (e.g. if this entity is known not to be relevant for the result)
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface SkipChecks {
}
