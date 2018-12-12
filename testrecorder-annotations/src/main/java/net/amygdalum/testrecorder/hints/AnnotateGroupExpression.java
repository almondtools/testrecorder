package net.amygdalum.testrecorder.hints;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is a hint for the Serializer:
 * - to record and persist additional attributes to an instance
 * 
 * This annotation is planned for the future
 * 
 * Use this annotation to provide additional filter attributes for the generation phase.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface AnnotateGroupExpression {
	String expression();
}
