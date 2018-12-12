package net.amygdalum.testrecorder.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is a hint for the MatcherGenerator:
 * - to skip the generation of a matcher for this entity
 * 
 * Use this annotation to keep tests concise, not generating matchers for fields that are irrelevant for the result
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface SkipChecks {
}
