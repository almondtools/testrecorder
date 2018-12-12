package net.amygdalum.testrecorder.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is a hint for the SetupGenerator:
 * - to treat the annotated method as setter method
 * 
 * Use this annotation to generate better readable tests. The generation prefers constructing objects
 * from constructor and setters over generic construction. With this annotation even setters with names that do
 * not follow the convention could be used.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Setter {
}
