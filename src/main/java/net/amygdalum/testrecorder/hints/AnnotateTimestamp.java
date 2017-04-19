package net.amygdalum.testrecorder.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating a method with this hint will instruct the deserializer
 * - to decorate a rendered test with all timestamps the corresponding test could be recorded
 * 
 * This is a future feature.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AnnotateTimestamp {
    String format() default "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
}
