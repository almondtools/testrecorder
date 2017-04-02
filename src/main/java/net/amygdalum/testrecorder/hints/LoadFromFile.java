package net.amygdalum.testrecorder.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating a type field, method result or param with this hint will instruct the deserializer
 * - to load the construction of the entity from file (using some kind of serialization reader)
 * 
 * This is a future feature.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface LoadFromFile {
    
    String writeTo() default "files";
    String readFrom() default "files";
}
