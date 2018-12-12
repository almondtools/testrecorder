package net.amygdalum.testrecorder.profile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is a hint for serialization and test generation:
 * - not to record the inner state of an annotated entity
 * 
 * Use this annotation to save time and memory for objects.
 * 
 * The setup generator should generate only a facade instance (all fields with default value)
 * The matcher generator should skip assertions on this instance
 * 
 *  Note that relevant input/output behavior of such objects should be recorded with {@link Input} or ({@link Output}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER })
public @interface Facade {
}
