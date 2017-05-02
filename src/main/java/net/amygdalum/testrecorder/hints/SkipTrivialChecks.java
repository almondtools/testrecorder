package net.amygdalum.testrecorder.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating a type field, method result or param with this hint will instruct the deserializer
 * - to skip the generation of a matcher for this entity if the check is classified as trivial (e.g. if there is no change in setup and matcher object)
 * 
 * This is a future feature.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface SkipTrivialChecks {
}
