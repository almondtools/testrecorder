package net.amygdalum.testrecorder.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is a hint for the SetupGenerator:
 * - to load the construction of the entity from file (using some kind of serialization reader)
 * 
 * Currently only supports primitive arrays.
 * 
 * Use this annotation to keep the test code free of large buffers, by instead loading such buffers from the file system.
 * This is only recommended if the data is large, binary or unreadable.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
public @interface LoadFromFile {

	String writeTo() default "files";

	String readFrom() default "files";
}
