package net.amygdalum.testrecorder.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating a method with this hint will instruct the deserializer
 * - to decorate a rendered test with a Data annotation containing the result of the expression given by value()
 * 
 * This is a future feature.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AnnotateGroupExpression {
    String expression();
}
